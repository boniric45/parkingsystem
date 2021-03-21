package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            assert ticket.getOutTime() != null;
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }
        Date inHour = ticket.getInTime(); // Date in parking
        Date outHour = ticket.getOutTime(); // Date out parking

        long inHourPark = inHour.getTime(); // convert inHour in milliseconds
        long outHourPark = outHour.getTime(); // convert outHour in milliseconds

        double freeMinutes = FreeMinutesServices.FreeMinutes(ticket.getFreeminute());

        //Calcul past time
        double duration = outHourPark - inHourPark; // past time
        duration -= freeMinutes; //subtract free minute from time spent
        duration = duration / 60 / 1000; //Convert to minutes

        //Calcul past time
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(Math.abs(Fare.CAR_RATE_PER_MINUTE * duration));

                break;
            }

            case BIKE: {

                ticket.setPrice(Math.abs(Fare.BIKE_RATE_PER_MINUTE * duration));
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}