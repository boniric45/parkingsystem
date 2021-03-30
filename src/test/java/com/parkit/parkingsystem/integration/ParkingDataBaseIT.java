package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    private static void tearDown() {
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1); //Type
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() throws Exception {

        //GIVEN
        String vehicleRegistrationNumber = "ABCDEF";
        String vehicleRegistrationNumberInBdd = inputReaderUtil.readVehicleRegistrationNumber();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingType parkingType = ParkingType.CAR;

        //WHEN
        setUp();
        setUpPerTest();
        parkingService.processIncomingVehicle();
        int nextParkingNumberInBdd = parkingSpotDAO.getNextAvailableSlot(parkingType);
        int parkingNumberInBDD = ticketDAO.getTicket(vehicleRegistrationNumber).getParkingSpot().getId();

        //THEN
        assertEquals(vehicleRegistrationNumberInBdd, vehicleRegistrationNumber);
        assertEquals(parkingNumberInBDD, nextParkingNumberInBdd - 1);
        System.out.println(" PARKING NUMBER > " + parkingNumberInBDD + " \n NEXT AVAILABLE SLOT > " + nextParkingNumberInBdd);
    }

    @Test
    public void testParkingLotExit() {

        //GIVEN
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        String vehiculeRegNumber = "ABCDEF";
        String fare = "0,75";
        Date outTimeNow = new Date();

        //WHEN
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date outTimeInBdd = ticketDAO.getTicket(vehiculeRegNumber).getOutTime();
        String dateNow = simpleDateFormat.format(outTimeNow);
        String dateInBdd = simpleDateFormat.format(outTimeInBdd);

        //Format price bdd two digits
        double priceInBdd = ticketDAO.getTicket(vehiculeRegNumber).getPrice();
        String fareInBdd = String.format("%.2f", priceInBdd);

        //THEN
        assertEquals(dateInBdd, dateNow);
        assertEquals(fareInBdd, fare);

    }

}
