package com.ticketservice.task;

import java.util.Map;



import java.util.Timer;
import java.util.TimerTask;

import com.ticketservice.dao.TicketServiceDAO;
import com.ticketservice.dto.SeatHold;
import com.ticketservice.service.TicketService;
import com.ticketservice.service.TicketServiceImpl;

public class SeatHeldReminderTask extends TimerTask {

	private int seatHoldId;
	private String customerEmail;
	private Timer timer;
	private TicketServiceDAO ticketServiceDAO;

	public SeatHeldReminderTask(int seatHoldId, String customerEmail,
			Timer timer,TicketServiceDAO ticketServiceDAO) {
		this.seatHoldId = seatHoldId;
		this.customerEmail = customerEmail;
		this.timer = timer;
		this.ticketServiceDAO=ticketServiceDAO;
	}

	@Override
	public void run() {
		
		Map<String, Map<Integer, SeatHold>> userHoldMap = ticketServiceDAO.getUserHoldMap();
				
		if (userHoldMap != null && !userHoldMap.isEmpty()) {
			Map<Integer, SeatHold> seatHoldMap = userHoldMap.get(customerEmail);
			if (seatHoldMap != null && !seatHoldMap.isEmpty()) {
				ticketServiceDAO.clearSeatHoldInfo(seatHoldMap.get(new Integer(
						seatHoldId)));
				ticketServiceDAO.getUserHoldMap().get(customerEmail)
						.remove(seatHoldId);
				System.out.println("Sorry.Your seat hold Id " + seatHoldId
						+ " expired");
			}

		}
		timer.cancel();
	}

}
