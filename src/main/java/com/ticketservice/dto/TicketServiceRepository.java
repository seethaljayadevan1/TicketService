package com.ticketservice.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Seethal 
 * This singleton class initializes the inMemoryData object
 * which represents the data model for the Ticket Service.It is also
 * used to access the data object
 */
public class TicketServiceRepository {
	private static TicketServiceRepository ticketServiceRepository;

	private InMemoryData inMemoryData;

	public InMemoryData getInMemoryData() {
		return inMemoryData;
	}

	private TicketServiceRepository() {
		initialize();
	}

	/**
	 * Initializes the data model for TicketService.
	 *         
	 *       
	 */
	private void initialize() {
		inMemoryData = new InMemoryData();
		Map<Integer, SeatLevelInfo> seatingInfoMap = inMemoryData
				.getSeatingInfoMap();
		if (inMemoryData.getSeatingInfoMap() == null) {
			seatingInfoMap = new HashMap<>();
			SeatLevelInfo levelSeatInfo = null;
			levelSeatInfo = populateLevelSeatInfo("Orchestra", 100.00, 25, 50);
			seatingInfoMap.put(new Integer(1), levelSeatInfo);
			levelSeatInfo = populateLevelSeatInfo("Main     ", 75.00, 20, 100);
			seatingInfoMap.put(new Integer(2), levelSeatInfo);
			levelSeatInfo = populateLevelSeatInfo("Balcony 1", 50.00, 15, 100);
			seatingInfoMap.put(new Integer(3), levelSeatInfo);
			levelSeatInfo = populateLevelSeatInfo("Balcony 2", 40.00, 15, 100);
			seatingInfoMap.put(new Integer(4), levelSeatInfo);
			inMemoryData.setSeatingInfoMap(seatingInfoMap);
		}
	}

	private String[][] getArray(int rowCount, int seatPerRow) {

		String[][] seatArray = new String[rowCount][seatPerRow];
		for (int row = 0; row < rowCount; row++) {
			for (int column = 0; column < seatPerRow; column++) {
				seatArray[row][column] = "A";
			}
		}
		return seatArray;

	}

	private SeatLevelInfo populateLevelSeatInfo(String name, Double price,
			int rows, int seatsPerRow) {
		SeatLevelInfo levelSeatInfo = new SeatLevelInfo();
		levelSeatInfo.setName(name);
		levelSeatInfo.setPrice(price);
		levelSeatInfo.setRowCount(rows);
		levelSeatInfo.setSeatsPerRowCount(seatsPerRow);
		levelSeatInfo.setTotalAvailableSeats(seatsPerRow * rows);
		levelSeatInfo.setSeatingArray(getArray(rows, seatsPerRow));
		return levelSeatInfo;
	}

	public static synchronized TicketServiceRepository getInstance() {
		if (ticketServiceRepository == null) {
			ticketServiceRepository = new TicketServiceRepository();
		}
		return ticketServiceRepository;
	}
}
