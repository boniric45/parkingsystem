package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.ConvertUtil;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {

        double duration = ConvertUtil.convertFreeMinutes(ticket); // return past time by minute - Free Minutes

        // Calcul Ticket
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