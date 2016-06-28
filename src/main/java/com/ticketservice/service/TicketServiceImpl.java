package com.ticketservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;

import com.ticketservice.constant.TicketServiceConstant;
import com.ticketservice.dao.TicketServiceDAO;
import com.ticketservice.dto.SeatHold;
import com.ticketservice.dto.SeatLevelInfo;
import com.ticketservice.exception.TicketServiceException;
import com.ticketservice.task.SeatHeldReminderTask;

public class TicketServiceImpl implements TicketService {

	private TicketServiceDAO ticketServiceDAO;

	public TicketServiceImpl(TicketServiceDAO ticketServiceDAO) {
		this.ticketServiceDAO = ticketServiceDAO;
	}

	
	public String reserveSeats(int seatHoldId, String customerEmail)throws TicketServiceException {
		// Fetch the seatHold object from the userholdMap.
		
		Map<Integer, SeatHold> seatHoldMap = ticketServiceDAO.getUserHoldMap()
				.get(customerEmail);
		if (seatHoldMap != null && !seatHoldMap.isEmpty()) {
			SeatHold seatHold = seatHoldMap.get(new Integer(seatHoldId));

			if (seatHold != null) {
				Map<Integer, List<String>> seatHeldMap = seatHold
						.getSeatHeldMap();

				if (seatHeldMap != null && !seatHeldMap.isEmpty()) {
					for (Integer level : seatHeldMap.keySet()) {
						List<String> seatsHeldInLevel = seatHeldMap.get(level);
						// Fetch the originial seatLevelMap for that Level
						SeatLevelInfo seatLevelInfo = ticketServiceDAO
								.getSeatingInfoMap().get(level);
						String[][] seatingArray = seatLevelInfo
								.getSeatingArray();
						for (String rowDetails : seatsHeldInLevel) {
							String[] rowDetail = rowDetails.split(":");
							int row = Integer.parseInt(rowDetail[0]);
							int column = Integer.parseInt(rowDetail[1]);
							if (seatingArray[row][column] == "H") {
								seatingArray[row][column] = "R";
							}
						}
						int totalAvailableSeats = seatLevelInfo
								.getTotalAvailableSeats();
						int heldSeats = seatLevelInfo.getHeldSeats();
						seatLevelInfo
								.setTotalAvailableSeats(totalAvailableSeats
										- seatsHeldInLevel.size());
						seatLevelInfo.setHeldSeats(heldSeats
								- seatsHeldInLevel.size());
						ticketServiceDAO.saveOrUpdateSeatingInfoMap(level,
								seatLevelInfo);
 
					}
				}
			}
			seatHoldMap.remove(new Integer(seatHoldId));
			int previousReservationCode = ticketServiceDAO.getReservationCode();
			ticketServiceDAO.setReservationCode(previousReservationCode + 1);
			ticketServiceDAO.saveOrUpdateReservationMap(new Integer(
					previousReservationCode), new Integer(seatHoldId));
			ticketServiceDAO.removeSeatHoldFromSeatHoldMap(customerEmail,
					seatHoldId);
			return Integer.toString(previousReservationCode);
		}else{
			
			new TicketServiceException("SeatHold information not found for the Seat Hold Id "+seatHoldId);
			return null;
		}
		
	}

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		int seatsAvailable = 0;
		if (venueLevel.isPresent()) {
			Map<Integer, SeatLevelInfo> seatingInfoMap = ticketServiceDAO
					.getSeatingInfoMap();
			if (seatingInfoMap != null && !seatingInfoMap.isEmpty()) {
				SeatLevelInfo seatLevelInfo = seatingInfoMap.get(venueLevel
						.get());
				if (seatLevelInfo != null) {
					seatsAvailable = (seatLevelInfo.getTotalAvailableSeats() - seatLevelInfo
							.getHeldSeats());
				}
			}
		} else {
			seatsAvailable = calculateTotalSeatsAllLevels();
		}
		return seatsAvailable;
	}
	
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel,
			Optional<Integer> maxLevel, String customerEmail) {

		SeatHold seatHold = new SeatHold();
		Map<Integer, List<String>> seatHeldMap = new HashMap<>();
		int minimumLevel = minLevel.get().intValue();
		int maximumLevel = maxLevel.get().intValue();
		double totalPrice = 0;

		seatHold.setCustomerEmail(customerEmail);
		seatHold.setRequestSeatCount(numSeats);
		for (int i = minimumLevel; i <= maximumLevel; i++) {
			// check if the seats are available in selected level
			SeatLevelInfo seatLevelInfo = getLevelSeatInfo(Optional.of(i));
			if (seatLevelInfo != null) {
				// Reducing the already held seats from available seats
				int heldSeats = seatLevelInfo.getHeldSeats();
				int availableSeats = seatLevelInfo.getTotalAvailableSeats()
						- heldSeats;
				// Proceed with seat holdonly if there are available seats
				if (availableSeats != 0) {

					if (availableSeats >= numSeats) {
						heldSeats = heldSeats + numSeats;
						seatLevelInfo.setHeldSeats(heldSeats);
						seatHeldMap.put(new Integer(i),
								heldSeatsInArray(numSeats, seatLevelInfo));
						ticketServiceDAO.saveOrUpdateSeatingInfoMap(
								new Integer(i), seatLevelInfo);
						totalPrice = totalPrice
								+ (seatLevelInfo.getPrice() * numSeats);
						numSeats=0;
						break;
					} else {
						heldSeats = heldSeats + availableSeats;
						seatLevelInfo.setHeldSeats(heldSeats);
						seatHeldMap
								.put(new Integer(i),
										heldSeatsInArray(availableSeats,
												seatLevelInfo));
						ticketServiceDAO.saveOrUpdateSeatingInfoMap(
								new Integer(i), seatLevelInfo);
						numSeats = numSeats - availableSeats;
						totalPrice = totalPrice + seatLevelInfo.getPrice()
								* availableSeats;

					}
				}
			}
		}

		int previousSeatHoldId = ticketServiceDAO.getPreviousSeatHoldId() + 1;
		ticketServiceDAO.setPreviousSeatHoldId(previousSeatHoldId);
		seatHold.setSeatHeldMap(seatHeldMap);
		seatHold.setTotalPrice(totalPrice);
		seatHold.setSeatHoldId(previousSeatHoldId);

		// Storing the seatHold information of different users in a single map
		Map<Integer, SeatHold> userSeatHoldMap = ticketServiceDAO
				.getUserHoldMap().get(customerEmail);
		if (userSeatHoldMap == null) {
			userSeatHoldMap = new HashMap<>();
		}
		ticketServiceDAO.saveOrUpdateUserHoldMap(
				new Integer(previousSeatHoldId), seatHold, customerEmail);
		if(numSeats>0){
			seatHold.setNonHeldSeatCount(numSeats);
			
		}
		return seatHold;

	}


	private int calculateTotalSeatsAllLevels() {
		int totalAvailableSeats = 0;
		Map<Integer, SeatLevelInfo> seatingInfoMap = ticketServiceDAO
				.getSeatingInfoMap();
		if (seatingInfoMap != null && !seatingInfoMap.isEmpty()) {
			for (Integer integerKey : seatingInfoMap.keySet()) {
				SeatLevelInfo seatLevelInfo = seatingInfoMap.get(integerKey);
				if (seatLevelInfo != null) {
					totalAvailableSeats = totalAvailableSeats
							+ (seatLevelInfo.getTotalAvailableSeats() - seatLevelInfo
									.getHeldSeats());
				}
			}
		}
		return totalAvailableSeats;
	}

	private List<String> heldSeatsInArray(int seats, SeatLevelInfo seatLevelInfo) {
		int rowCount = seatLevelInfo.getRowCount();
		int columnCount = seatLevelInfo.getSeatsPerRowCount();
		String[][] seatArray = seatLevelInfo.getSeatingArray();
		List<String> seatNumberList = new ArrayList<>();

		for (int row = 0; row < rowCount; row++) {
			if (seats > 0) {
				for (int column = 0; column < columnCount; column++) {
					if ("A".equalsIgnoreCase(seatArray[row][column])) {
						// Seat found available and is marked held
						seatArray[row][column] = "H";
						seatNumberList.add(row + ":" + column);
						seats--;
						if (seats == 0) {
							break;
						}
					}
				}
			}
		}
		return seatNumberList;

	}
	
	public void startTimer(int seatHoldId, String customerEmail) {
		Timer timer = new Timer();
		timer.schedule(new SeatHeldReminderTask(seatHoldId, customerEmail,
				timer, ticketServiceDAO), TicketServiceConstant.HOLD_OUT_TIME_MILLIS);
	}

	public void clearSeatHeld() {
		String loggedCustomerEmail = DisplayService.getLoggedInUserName();
		// Get seatHoldMap details of the user;
		Map<Integer, SeatHold> seatHoldMap = ticketServiceDAO.getUserHoldMap()
				.get(loggedCustomerEmail);
		if (seatHoldMap != null && !seatHoldMap.isEmpty()) {
			Iterator<Integer> iterator = seatHoldMap.keySet().iterator();
			while (iterator.hasNext()) {
				Integer seatHoldId = iterator.next();
				if (seatHoldId != null) {
					ticketServiceDAO.clearSeatHoldInfo(seatHoldMap
							.get(seatHoldId));
					iterator.remove();
				}

			}
		}
		ticketServiceDAO.removeUserFromUserHoldMap(loggedCustomerEmail);

	}

	
	private SeatLevelInfo getLevelSeatInfo(Optional<Integer> level) {
		return ticketServiceDAO.getSeatingInfoMap().get(level.get());
	}

	public Map<Integer,SeatLevelInfo> getSeatingInfoMap(){
		return ticketServiceDAO.getSeatingInfoMap();
	}
	
}
