package com.ticketservice.dto;

public class SeatLevelInfo {
	private String name;
	private int rowCount;
	private int seatsPerRowCount;
	private double price;
	private int totalAvailableSeats;
	private int heldSeats;
	private int reservedSeats;
	private String[][] seatingArray;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getSeatsPerRowCount() {
		return seatsPerRowCount;
	}

	public void setSeatsPerRowCount(int seatsPerRowCount) {
		this.seatsPerRowCount = seatsPerRowCount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTotalAvailableSeats() {
		return totalAvailableSeats;
	}

	public void setTotalAvailableSeats(int totalAvailableSeats) {
		this.totalAvailableSeats = totalAvailableSeats;
	}

	public int getHeldSeats() {
		return heldSeats;
	}

	public void setHeldSeats(int heldSeats) {
		this.heldSeats = heldSeats;
	}

	public int getReservedSeats() {
		return reservedSeats;
	}

	public void setReservedSeats(int reservedSeats) {
		this.reservedSeats = reservedSeats;
	}

	public String[][] getSeatingArray() {
		return seatingArray;
	}

	public void setSeatingArray(String[][] seatingArray) {
		this.seatingArray = seatingArray;
	}
}
