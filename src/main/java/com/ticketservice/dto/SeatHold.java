package com.ticketservice.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatHold {
	private int SeatHoldId;
	private double totalPrice;
	private String customerEmail;
	private int requestSeatCount;
	private int nonHeldSeatCount;
	
	private Map<Integer, List<String>> seatHeldMap = new HashMap<>();

	public int getSeatHoldId() {
		return SeatHoldId;
	}

	public void setSeatHoldId(int seatHoldId) {
		SeatHoldId = seatHoldId;
	}
	public int getNonHeldSeatCount() {
		return nonHeldSeatCount;
	}

	public void setNonHeldSeatCount(int nonHeldSeatCount) {
		this.nonHeldSeatCount = nonHeldSeatCount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public int getRequestSeatCount() {
		return requestSeatCount;
	}

	public void setRequestSeatCount(int requestSeatCount) {
		this.requestSeatCount = requestSeatCount;
	}

	public Map<Integer, List<String>> getSeatHeldMap() {
		return seatHeldMap;
	}

	public void setSeatHeldMap(Map<Integer, List<String>> seatHeldMap) {
		this.seatHeldMap = seatHeldMap;
	}

}
