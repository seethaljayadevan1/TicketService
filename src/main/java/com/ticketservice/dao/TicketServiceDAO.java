package com.ticketservice.dao;

import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ticketservice.dto.SeatHold;
import com.ticketservice.dto.SeatLevelInfo;
import com.ticketservice.dto.TicketServiceRepository;
import com.ticketservice.service.DisplayService;



/**
 * @author Seethal
 * DAO class which accesses the TicketServiceRepository for the data manipulations
 *
 */
public class TicketServiceDAO {
	private TicketServiceRepository ticketRepository;

	public TicketServiceDAO(TicketServiceRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	public Map<String, Map<Integer, SeatHold>> getUserHoldMap() {
		return ticketRepository.getInMemoryData().getUserHoldMap();
	}

	public Map<Integer, SeatLevelInfo> getSeatingInfoMap() {
		return ticketRepository.getInMemoryData().getSeatingInfoMap();
	}

	public void removeUserFromUserHoldMap(String customerEmail) {
		ticketRepository.getInMemoryData().getUserHoldMap()
				.remove(customerEmail);
	}

	public void removeSeatHoldFromSeatHoldMap(String customerEmail,
			int SeatHoldId) {
		ticketRepository.getInMemoryData().getUserHoldMap().get(customerEmail)
				.remove(SeatHoldId);
	}

	public void saveOrUpdateSeatingInfoMap(Integer level,
			SeatLevelInfo seatLevelInfo) {
		ticketRepository.getInMemoryData().getSeatingInfoMap()
				.put(level, seatLevelInfo);
	}
	
	public void clearSeatHeld() {
		String loggedCustomerEmail = DisplayService.getLoggedInUserName();
		// Get seatHoldMap details of the user;
		Map<Integer, SeatHold> seatHoldMap = getUserHoldMap()
				.get(loggedCustomerEmail);
		if (seatHoldMap != null && !seatHoldMap.isEmpty()) {
			Iterator<Integer> iterator = seatHoldMap.keySet().iterator();
			while (iterator.hasNext()) {
				Integer seatHoldId = iterator.next();
				if (seatHoldId != null) {
					clearSeatHoldInfo(seatHoldMap
							.get(seatHoldId));
					iterator.remove();
				}

			}
		}
		removeUserFromUserHoldMap(loggedCustomerEmail);

	}
 

	public void saveOrUpdateUserHoldMap(Integer seatHoldId, SeatHold seatHold,
			String customerEmail) {
		Map<Integer, SeatHold> seatHoldMap = ticketRepository.getInMemoryData()
				.getUserHoldMap().get(customerEmail);
		if (seatHoldMap == null) {
			seatHoldMap = new HashMap<>();
			seatHoldMap.put(seatHoldId, seatHold);
			ticketRepository.getInMemoryData().getUserHoldMap()
					.put(customerEmail, seatHoldMap);
		} else {
			ticketRepository.getInMemoryData().getUserHoldMap()
					.get(customerEmail).put(seatHoldId, seatHold);
		}

	}

	public void setReservationCode(int previousReservationCode) {
		ticketRepository.getInMemoryData().setPreviousReservationCode(
				previousReservationCode);
	}

	public int getPreviousSeatHoldId() {
		return ticketRepository.getInMemoryData().getPreviousSeatHoldId();
	}

	public void clearSeatHoldInfo(SeatHold seatHold) {
		if (seatHold != null) {
			Map<Integer, List<String>> seatHeldMap = seatHold.getSeatHeldMap();

			if (seatHeldMap != null && !seatHeldMap.isEmpty()) {
				for (Integer level : seatHeldMap.keySet()) {
					List<String> seatsHeldInLevel = seatHeldMap.get(level);
					// Fetch the originial seatLevelMap for that Level
					SeatLevelInfo seatLevelInfo = ticketRepository
							.getInMemoryData().getSeatingInfoMap().get(level);
					String[][] seatingArray = seatLevelInfo.getSeatingArray();
					for (String rowDetails : seatsHeldInLevel) {
						String[] rowDetail = rowDetails.split(":");
						int row = Integer.parseInt(rowDetail[0]);
						int column = Integer.parseInt(rowDetail[1]);
						if (seatingArray[row][column] == "H") {
							seatingArray[row][column] = "A";
						}
					}
					int heldSeats = seatLevelInfo.getHeldSeats();
					seatLevelInfo.setHeldSeats(heldSeats
							- seatsHeldInLevel.size());
					saveOrUpdateSeatingInfoMap(level, seatLevelInfo);
				}
			}
		}
	}

	public void setPreviousSeatHoldId(int seatHoldId) {
		ticketRepository.getInMemoryData().setPreviousSeatHoldId(seatHoldId);
	}

	public int getReservationCode() {
		return ticketRepository.getInMemoryData().getPreviousReservationCode();
	}

	public void saveOrUpdateReservationMap(Integer reservationCode,
			Integer seatHoldId) {
		ticketRepository.getInMemoryData().getReservationMap()
				.put(reservationCode, seatHoldId);
	}

}
