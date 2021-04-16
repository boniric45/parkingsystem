package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.ConvertUtil;

public class FareCalculatorService {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void calculateFare(Ticket ticket, boolean reduction) {

        double duration = ConvertUtil.convertFreeMinutes(ticket); // return past time by minute - Free Minutes
        double reductionFare = 0;
        double finalFareWithReduction = 0;
        double finalFareWithoutReduction = 0;
        int montantReduction = 5;

        // Calcul Ticket
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                if (reduction) {
                    reductionFare = Fare.CAR_RATE_PER_MINUTE * duration * montantReduction / 100;
                    finalFareWithReduction = (Fare.CAR_RATE_PER_MINUTE * duration) - reductionFare;
                    ticket.setPrice(round(finalFareWithReduction, 2));
                } else {
                    finalFareWithoutReduction = Fare.CAR_RATE_PER_MINUTE * duration;
                    ticket.setPrice(round(finalFareWithoutReduction, 2));
                }
                break;
            }
            case BIKE: {
                if (reduction) {
                    reductionFare = Fare.BIKE_RATE_PER_MINUTE * duration * montantReduction / 100;
                    finalFareWithReduction = (Fare.BIKE_RATE_PER_MINUTE * duration) - reductionFare;
                    ticket.setPrice(round(finalFareWithReduction, 2));
                } else {
                    finalFareWithoutReduction = Fare.BIKE_RATE_PER_MINUTE * duration;
                    ticket.setPrice(round(finalFareWithoutReduction, 2));
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}