package com.ticketservice;


import com.ticketservice.dto.TicketServiceRepository;


import com.ticketservice.exception.TicketServiceException;
import com.ticketservice.service.DisplayService;
import com.ticketservice.service.TicketService;
import com.ticketservice.service.TicketServiceImpl;
import com.ticketservice.dao.TicketServiceDAO;

/**
 * @author Seethal Starting point of the Ticket Service Application
 */
public class MainApp {
	public static void main(String[] args) {
		try {
			TicketServiceRepository ticketServiceRepository = TicketServiceRepository
					.getInstance();
			TicketServiceDAO ticketServiceDAO = new TicketServiceDAO(
					ticketServiceRepository);
			TicketService ticketService = new TicketServiceImpl(
					ticketServiceDAO);
			DisplayService displayService = new DisplayService(ticketService);
			displayService.displayAndProcessLoginInfo();
		} catch (TicketServiceException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out
					.println("Error occured in the ticket service application");
		}
	}

}
