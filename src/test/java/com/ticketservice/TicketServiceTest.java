package com.ticketservice;

import java.util.Optional;

import com.ticketservice.dao.TicketServiceDAO;
import com.ticketservice.dto.SeatHold;
import com.ticketservice.dto.TicketServiceRepository;
import com.ticketservice.exception.TicketServiceException;
import com.ticketservice.service.TicketService;
import com.ticketservice.service.TicketServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class TicketServiceTest 
   
{
	TicketService ticketService;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    

    /**
     * @return the suite of tests being tested
     */
   
   @Before
    public void setUp() throws Exception {
    	TicketServiceRepository ticketServiceRepository = TicketServiceRepository
				.getInstance();
		TicketServiceDAO ticketServiceDAO = new TicketServiceDAO(
				ticketServiceRepository);
		ticketService = new TicketServiceImpl(ticketServiceDAO);
    	
    	
	}
    /**
     * Rigourous Test :-)
     */
    @Test
    public void numSeatsAvailable()
    {
        assertEquals(ticketService.numSeatsAvailable(Optional.of(1)),1250 );
        assertEquals(ticketService.numSeatsAvailable(Optional.of(2)),2000 );
        assertEquals(ticketService.numSeatsAvailable(Optional.of(3)),1500 );
        assertEquals(ticketService.numSeatsAvailable(Optional.of(4)),1500 );
        SeatHold seatHold=ticketService.findAndHoldSeats(5, Optional.of(1), Optional.of(2), "sheethal.jayadevan@gmail.com");
        assertNotNull(seatHold);
        try {
			assertNotNull(ticketService.reserveSeats(seatHold.getSeatHoldId(), "sheethal.jayadevan@gmail.com"));
		} catch (TicketServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
}
