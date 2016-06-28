package com.ticketservice.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ticketservice.constant.TicketServiceConstant;
import com.ticketservice.dao.TicketServiceDAO;
import com.ticketservice.dto.SeatHold;
import com.ticketservice.dto.SeatLevelInfo;
import com.ticketservice.exception.TicketServiceException;

public class DisplayService {
	private static String loggedInUserName;
	private TicketService ticketService;

	public DisplayService(TicketService ticketService) {
		this.ticketService = ticketService;

	}

	public static String getLoggedInUserName() {
		return loggedInUserName;
	}

	public static void setLoggedInUserName(String loggedInUserName) {
		DisplayService.loggedInUserName = loggedInUserName;
	}

	public void displayMainMenu() {

		System.out.println("TICKET SERVICE SYSTEM MAIN MENU");
		System.out.println("*******************************");
		System.out.println("[1] Check Availability         ");
		System.out.println("[2] Find And Hold Seats        ");
		System.out.println("[3] Reserve Seats              ");
		System.out.println("[4] Logout              ");
		System.out.println();

	}

	public String getCustomerEmail() {
		return validateCustomerEmail();
	}

	public String getPassword() {
		System.out.print("Password:");
		String customerPassword = getReader().read();
		return customerPassword;
	}

	public void displayAndProcessLoginInfo() throws TicketServiceException {
		DisplayService.loggedInUserName = null;
		System.out.println("Login in to the Ticket Service");
		System.out.println("*******************************");
		String customerEmail = getCustomerEmail();
		String customerPassword = getPassword();
		if (!(customerEmail != null && customerPassword
				.equalsIgnoreCase("password"))) {
			System.out.println("Login not successful ,please try again:");
			displayAndProcessLoginInfo();
		} else {
			DisplayService.loggedInUserName = customerEmail;
			displaySeatLevelInfo();
			displayMainMenu();
			processMainMenuChoices();
		}

	}

	public void displaySeatLevelInfo() {
		System.out.println();
		System.out.println("*********Seating Details*********");
		Map<Integer, SeatLevelInfo> seatingInfoMap = ticketService
				.getSeatingInfoMap();
		if (seatingInfoMap != null && !seatingInfoMap.isEmpty()) {
			System.out.print("Level ");
			System.out.print(" Name ");
			System.out.print(" Price ");
			System.out.print("AvailableSeats ");
			System.out.println();
			for (Integer level : seatingInfoMap.keySet()) {
				SeatLevelInfo seatLevelInfo = seatingInfoMap.get(level);
				if (seatLevelInfo != null) {

					System.out.print("   " + level);
					System.out.print("    " + seatLevelInfo.getName());
					System.out.print("    " + seatLevelInfo.getPrice());
					System.out.print("    "
							+ seatLevelInfo.getTotalAvailableSeats());
					System.out.println();

				}
			}
			System.out.println();
		}

	}

	public String validateMenuChoice() {
		String choice = null;
		boolean isFirstAttempt = true;
		while (StringUtils.isEmpty(choice)) {
			if (isFirstAttempt) {
				System.out.print("Enter your choice :");
			} else {
				System.out.print("Invalid Choice,Please try again :");
			}
			choice = getReader().read();
			isFirstAttempt = false;
		}
		return choice;
	}

	public String validateCustomerEmail() {
		String customerEmail = null;
		boolean isFirstAttempt = true;
		
		while (!validateId(customerEmail)) {

			if (isFirstAttempt) {
				System.out.print("Customer Email:");
			} else {
				System.out.print("Enter a valid Customer Email");
			}
			customerEmail = getReader().read();
			isFirstAttempt = false;
		}
		return customerEmail;
	}

	private boolean validateId(String customerEmail) {
		boolean isValid = false;
		if (!StringUtils.isEmpty(customerEmail)) {
			Pattern pattern = Pattern
					.compile("[a-zA-Z]*[0-9]*@[a-zA-Z]*.[a-zA-Z]*");
			Matcher matcher = pattern.matcher(customerEmail);
			if (matcher.matches()) {
				isValid = true;
			}
		}
		return isValid;
	}

	public void processMainMenuChoices() throws TicketServiceException {
		String choice = validateMenuChoice();
		int menuChoice;
		if (choice != null && !choice.isEmpty()) {
			try{
				menuChoice = Integer.parseInt(choice);
		    }catch(NumberFormatException e){
		    	menuChoice = 0;
		    }

			
			switch (menuChoice) {
			case 1:
				displayAvailabilityMenu();
				break;

			case 2:
				;
				SeatHold seatHold = displayFindAndHoldSeatMenu();
				seatHeldReport(seatHold);
				System.out.println("Seat held will expire in "
						+ TicketServiceConstant.HOLD_OUT_TIME
						+ ".Please reserve your seats");
				ticketService.startTimer(seatHold.getSeatHoldId(),
						seatHold.getCustomerEmail());
				break;

			case 3:
				displayReserveSeatMenu();
				break;

			case 4: // Clear the seatHelds by the logged In user
				ticketService.clearSeatHeld();
				System.out.print("You have logged out successfully");
				displayAndProcessLoginInfo();
				getReader().getScanner().close();
				break;
			default :
				System.out.print("Invalid choice ");
			}

			if (confirmBackToMainMenu("Return to main menu ? (Y/N)")) {
				displayMainMenu();
				processMainMenuChoices();
			} else {
				ticketService.clearSeatHeld();
				System.out.print("You have logged out successfully");
				displayAndProcessLoginInfo();
			}
		}

	}

	public boolean confirmBackToMainMenu(String message) {
		boolean mainMenuConfirm = false;
		System.out.print(message);
		String confirmMenu = getReader().read();
		if (confirmMenu != null && confirmMenu.equalsIgnoreCase("Y")) {
			mainMenuConfirm = true;

		}
		return mainMenuConfirm;
	}

	public SeatHold displayFindAndHoldSeatMenu() {

		System.out.print("Enter the number of seats:");
		int seatsToHold = getReader().readInt();
		System.out.print("Enter the Minimum seating Level ID:");
		int minLevelId = getReader().readInt();
		System.out.print("Enter the Maximum seating Level ID:");
		int maxLevelId = getReader().readInt();

		return ticketService.findAndHoldSeats(seatsToHold,
				Optional.of(minLevelId), Optional.of(maxLevelId),
				DisplayService.loggedInUserName);
	}

	public void displayReserveSeatMenu() throws TicketServiceException {
		try {
			System.out.println();
			String customerEmail = DisplayService.loggedInUserName;
			System.out.println("Enter seatHoldId:");
			int seatHoldId = getReader().readInt();
			ticketService.reserveSeats(seatHoldId, customerEmail);
			System.out.println("Reserved seats successfully");

		} catch (TicketServiceException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public void printSeatsInLevel(Integer levelId) {

		Map<Integer, SeatLevelInfo> seatingInfoMap = ticketService
				.getSeatingInfoMap();

		if (seatingInfoMap != null && !seatingInfoMap.isEmpty()) {
			SeatLevelInfo seatLevelInfo = seatingInfoMap.get(levelId);
			if (seatLevelInfo != null) {
				int row = seatLevelInfo.getRowCount();
				int column = seatLevelInfo.getSeatsPerRowCount();
				String[][] seatArray = seatLevelInfo.getSeatingArray();
				for (int rowCount = 0; rowCount < row; rowCount++) {
					int displayRow = rowCount + 1;
					System.out.print("Row " + displayRow + " - ");
					for (int columnCount = 0; columnCount < column; columnCount++) {
						System.out.print("[" + seatArray[rowCount][columnCount]
								+ "], ");
					}
					System.out.println();
				}

			}
		}

	}

	public void displayAvailabilityMenu() {

		System.out.print("Enter the level number : ");
		String levelId = getReader().read();
		Integer integer = null;
		System.out.println();
		System.out.println("Seats Available     ");
		System.out.println("*********************");
		if (levelId != null && !levelId.isEmpty()) {
			integer = Integer.parseInt(levelId);
			System.out.println("Level Selected :   " + levelId);
			printSeatsInLevel(Integer.parseInt(levelId));
		} else {
			System.out.println("All   Levels         ");
		}
		System.out
				.println("Available Seats  :"
						+ ticketService.numSeatsAvailable(Optional
								.ofNullable(integer)));

	}

	public void seatHeldReport(SeatHold seatHold) {

		if (seatHold.getSeatHeldMap().isEmpty()) {
			System.out.println("No seats available in the requested level:");
		} else {
			System.out.print("Seat Held Details ");
			System.out.println("*********************");
			System.out.println("User name:" + seatHold.getCustomerEmail());
			System.out.println("Seat Hold Id:" + seatHold.getSeatHoldId());
			System.out.println("Total Price:" + seatHold.getTotalPrice());
			System.out.println("Seat Details ");
			System.out.println("*****************");
			if (seatHold.getNonHeldSeatCount() > 0) {
				int seatsHeldCount = seatHold.getRequestSeatCount()
						- seatHold.getNonHeldSeatCount();
				System.out
						.println("Only "
								+ seatsHeldCount
								+ " seats could be held.Seats full in the requested levels");
			}
			for (Integer level : seatHold.getSeatHeldMap().keySet()) {
				System.out.println("Level: " + level + "  ");
				System.out.println();
				List<String> rowDetails = seatHold.getSeatHeldMap().get(level);
				if (rowDetails != null && !rowDetails.isEmpty()) {
					for (String rowDetail : rowDetails) {
						if (rowDetail != null && !rowDetail.isEmpty()) {
							String rowArray[] = rowDetail.split(":");
							int row = rowArray[0] == null ? null : (Integer
									.parseInt(rowArray[0]) + 1);
							int column = rowArray[1] == null ? null : (Integer
									.parseInt(rowArray[1]) + 1);
							System.out.print("Row[" + row + "]Seat[" + column
									+ "],");
						}
					}
				}
				System.out.println();

			}

		}

	}

	public Reader getReader() {
		Reader reader = new Reader();
		return reader;
	}
}
