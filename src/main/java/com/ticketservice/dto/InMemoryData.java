package com.ticketservice.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Seethal
 * This class represents in memory data/database holding details for
 * the Ticket Service
 */
public class InMemoryData {
	private  Map<String, Map<Integer, SeatHold>> userHoldMap = new HashMap<>();
	private  Map<Integer, SeatLevelInfo> seatingInfoMap ;
	private Map<Integer, Integer> reservationMap = new HashMap<>();
	private  int previousSeatHoldId;
	private  int previousReservationCode;
	public Map<String, Map<Integer, SeatHold>> getUserHoldMap() {
		return userHoldMap;
	}
	public void setUserHoldMap(Map<String, Map<Integer, SeatHold>> userHoldMap) {
		this.userHoldMap = userHoldMap;
	}
	public Map<Integer, SeatLevelInfo> getSeatingInfoMap() {
		return seatingInfoMap;
	}
	public void setSeatingInfoMap(Map<Integer, SeatLevelInfo> seatingInfoMap) {
		this.seatingInfoMap = seatingInfoMap;
	}
	public Map<Integer, Integer> getReservationMap() {
		return reservationMap;
	}
	public void setReservationMap(Map<Integer, Integer> reservationMap) {
		this.reservationMap = reservationMap;
	}
	public int getPreviousSeatHoldId() {
		return previousSeatHoldId;
	}
	public void setPreviousSeatHoldId(int previousSeatHoldId) {
		this.previousSeatHoldId = previousSeatHoldId;
	}
	public int getPreviousReservationCode() {
		return previousReservationCode;
	}
	public void setPreviousReservationCode(int previousReservationCode) {
		this.previousReservationCode = previousReservationCode;
	}
}
