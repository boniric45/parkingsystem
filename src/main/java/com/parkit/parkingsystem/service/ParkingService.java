package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.HasReductionUtil;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;


/**
 * ParkingService manages vehicle entrances and exits.
 *
 * @author Eric
 * @version 1.0
 */
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");
    private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();
    private final InputReaderUtil inputReaderUtil;
    private final ParkingSpotDAO parkingSpotDAO;
    private final TicketDAO ticketDAO;


    /**
     * Constructor.
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * register the entry of a vehicle with the parking space, date and time.
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                Date inTime = new Date();
                Ticket ticket = new Ticket();
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number: " + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number: " + vehicleRegNumber + " is: " + inTime);
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * return the vehicle registration entry
     */
    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * get the next parking number if available
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    /**
     * Get the vehichle type
     */
    private ParkingType getVehichleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    /**
     * register the exit of a vehicle, update the parking space and the ticket.
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Date outTime = new Date();
            ticket.setOutTime(outTime);
            boolean reductionFivePourcent = HasReductionUtil.hasReduction(vehicleRegNumber);  // verify elligibility
            fareCalculatorService.calculateFare(ticket, reductionFivePourcent);

            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Parking Number : " + parkingSpot.getId());
                System.out.println("Please pay the parking fare: " + ticket.getPrice());
                System.out.println("Ticket with 30 free minutes");
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                logger.error("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }
}
