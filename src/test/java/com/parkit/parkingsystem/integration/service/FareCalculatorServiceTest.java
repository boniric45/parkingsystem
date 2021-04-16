package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.ConvertUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {
    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void testCalculateFareCar() {

        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 Hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setFreeminute(30);
        fareCalculatorService.calculateFare(ticket, false);

        //THEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_MINUTE * 30);// 1 Hour - Free Minute = 30 Minutes
    }

    @Test
    public void testCalculateFareBike() {
        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 Hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setFreeminute(30);
        fareCalculatorService.calculateFare(ticket, false);

        //THEN
        assertEquals(ticket.getPrice(), FareCalculatorService.round(Fare.BIKE_RATE_PER_MINUTE * 30, 1));// 1 Hour - Free Minute = 30 Minutes
    }

    @Test
    public void testCalculateFareUnkownType() {

        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //THEN
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void testCalculateFareBikeWithFutureInTime() {

        //GIVEN
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void testCalculateFareBikeWithLessThanOneHourParkingTime() {

        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        Date inTime = new Date();
        Date outTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Ticket ticket = new Ticket();

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setFreeminute(30);

        double tt = ConvertUtil.convertFreeMinutes(ticket);
        System.out.println("In: " + ticket.getInTime() + "\nOut: " + ticket.getOutTime() + "\nCU: " + tt);
        fareCalculatorService.calculateFare(ticket, false);

        //THEN
        assertEquals(round(Fare.BIKE_RATE_PER_MINUTE * 15, 2), ticket.getPrice());
    }

    @Test
    public void testCalculateFareCarWithLessThanOneHourParkingTime() {

        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTime = new Date();
        Date outTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setFreeminute(30);
        fareCalculatorService.calculateFare(ticket, false);

        //THEN
        assertEquals(round(Fare.CAR_RATE_PER_MINUTE * 15, 2), ticket.getPrice()); //45 minutes - free minutes > 15 minutes
    }

    @Test
    public void testCalculateFareCarWithMoreThanADayParkingTime() {

        //GIVEN
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Date inTime = new Date();
        Date outTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour

        //WHEN
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setFreeminute(30);
        fareCalculatorService.calculateFare(ticket, false);

        //THEN
        assertEquals(((24 * 60 * Fare.CAR_RATE_PER_MINUTE) - (Fare.CAR_RATE_PER_MINUTE * 30)), ticket.getPrice()); // 24 Hours - 30 Free minutes
    }


}
