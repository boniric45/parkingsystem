package com.parkit.parkingsystem.service;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.ConvertUtil;

import java.text.DecimalFormat;

public class FareCalculatorService {
    
    public void calculateFare(Ticket ticket, boolean reduction) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
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
                    finalFareWithReduction = Fare.CAR_RATE_PER_MINUTE * duration - reductionFare;
                    df.format(finalFareWithReduction);
                    ticket.setPrice(finalFareWithReduction);
                    System.out.println("Final > "+finalFareWithReduction);

                } else {
                    finalFareWithoutReduction = Fare.CAR_RATE_PER_MINUTE * duration;
                    ticket.setPrice(finalFareWithoutReduction);
                }
                break;
            }
            case BIKE: {

                if (reduction) {
                    reductionFare = Math.abs(Fare.BIKE_RATE_PER_MINUTE * duration * montantReduction / 100);
                    ticket.setPrice(Math.abs(Fare.BIKE_RATE_PER_MINUTE * duration - reductionFare));
                } else {
                    ticket.setPrice(Math.abs(Fare.BIKE_RATE_PER_MINUTE * duration));
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}