package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.integration.config.HasReductionUtilTest;
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
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String vehiculeRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
        Ticket ticket = new Ticket();
        TicketDAO ticketDAO = new TicketDAO();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTime = new Date();
        double fare = 0;

        //WHEN
        ticket.setInTime(inTime);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        double priceInBdd = round(ticket.getPrice(),2);

        //THEN
        assertEquals(fare, priceInBdd);
    }

    @Test
    public void testFareFivePercent() throws Exception {

        //GIVEN
        Date inTime = new Date(System.currentTimeMillis() - (7 * 60 * 60 * 1000)); // 7 Hours
        Date outTime = new Date();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        String vehiculeRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        Ticket ticket = new Ticket();
        int nbrTicketCreate = 3;
        double fareExpected = 6.18;
        boolean reducElligible=false;

        //WHEN
        //Create 3 tickets with vehicleRegNumber in bdd test
        for (int i = 1; i < nbrTicketCreate + 1; i++) {
            parkingSpot = new ParkingSpot(i, ParkingType.BIKE, false);
            ticket.setVehicleRegNumber(vehiculeRegNumber);
            ticket.setParkingSpot(parkingSpot);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            reducElligible = HasReductionUtilTest.hasReduction(vehiculeRegNumber); // elligible reduction
            fareCalculatorService.calculateFare(ticket,reducElligible); // Calcul ticket
            parkingSpot.setAvailable(true);
            parkingSpotDAO.updateParking(parkingSpot);
            ticketDAO.saveTicket(ticket);
           }

        //Search ticket number in bdd
        int numberTicketFind = ticketDAO.getTicketDatabase(vehiculeRegNumber);//Number Find db recette
        double fareBdd = round(ticket.getPrice(),2);

        //THEN
        assertEquals(nbrTicketCreate, numberTicketFind);
        assertEquals(fareExpected,fareBdd);
        assertTrue(reducElligible);
    }
}
