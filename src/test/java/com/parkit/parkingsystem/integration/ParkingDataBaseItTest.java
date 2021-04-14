package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.HasReductionUtilTest;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareServiceTest;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseItTest {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareServiceTest dataBasePrepareServiceTest;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareServiceTest = new DataBasePrepareServiceTest();
    }

    @AfterAll
    private static void tearDown() {
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1); //Type
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareServiceTest.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() throws Exception {

        //GIVEN
        String vehicleRegistrationNumber = "ABCDEF";
        String vehicleRegistrationNumberInBdd = inputReaderUtil.readVehicleRegistrationNumber();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //WHEN
        setUp();
        setUpPerTest();
        parkingService.processIncomingVehicle();
        int parkingNumberInBDD = ticketDAO.getTicket(vehicleRegistrationNumber).getParkingSpot().getId();

        //THEN
        assertEquals(vehicleRegistrationNumber, vehicleRegistrationNumberInBdd);
        assertEquals(1, parkingNumberInBDD);
    }

    @Test
    public void testParkingLotExit() throws Exception {

        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        String vehiculeRegNumber = "ABCDEF";
        String fare = "0,75";
        Date outTimeNow = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //WHEN
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        ticketDAO.saveTicket(ticketDAO.getTicket(vehiculeRegNumber));
        Date outTimeInBdd = ticketDAO.getTicket(vehiculeRegNumber).getOutTime();
        System.out.println("Outimebdd > " + outTimeInBdd);
        System.out.println("> " + ticketDAO.saveTicket(ticketDAO.getTicket(vehiculeRegNumber)));
        String dateNow = simpleDateFormat.format(outTimeNow);
        String dateInBdd = simpleDateFormat.format(outTimeInBdd);

        //Format price bdd two digits
        double priceInBdd = ticketDAO.getTicket(vehiculeRegNumber).getPrice();
        String fareInBdd = String.format("%.2f", priceInBdd);

        //THEN
        assertEquals(dateNow, dateInBdd);
        assertEquals(fare, fareInBdd);
    }

    @Test
    public void testFareFivePercent() throws Exception {

        //GIVEN
        Date inTime = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); // 24 Hours
        Date outTime = new Date();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        String vehiculeRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        int nbrTicketCreate = 3;
        String fareExpected = "22,33";

        //WHEN
        //Create 3 tickets with vehicleRegNumber in bdd,
        for (int i = 1; i < nbrTicketCreate + 1; i++) {
            parkingSpot = new ParkingSpot(i, ParkingType.BIKE, false);
            ticket.setVehicleRegNumber(vehiculeRegNumber);
            ticket.setParkingSpot(parkingSpot);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket, false);
            parkingSpot.setAvailable(true);
            parkingSpotDAO.updateParking(parkingSpot);
            ticketDAO.saveTicket(ticket);
            //System.out.println("\nTicket intime > " + ticket.getInTime() + "\n" + "Ticket outtime > " + ticket.getOutTime() + "\n" + "Price : " + ticket.getPrice() + "\n" + "Parking Number: " + parkingSpot.getId() + "\n");
        }

        //Search ticket number in bdd
        vehiculeRegNumber = "ABCDEF";
        int numberTicketFind = ticketDAO.getTicketDatabase(vehiculeRegNumber);//Number Find db recette
        boolean reducElligible = HasReductionUtilTest.hasReduction(vehiculeRegNumber); // elligible reduction
        fareCalculatorService.calculateFare(ticket,reducElligible); // Calcul ticket



        String fare = String.format("%.2f", ticket.getPrice()); // Format price two digit

        //THEN
        assertEquals(nbrTicketCreate, numberTicketFind);
        assertEquals(fareExpected,fare);
        assertTrue(reducElligible);
    }
}
