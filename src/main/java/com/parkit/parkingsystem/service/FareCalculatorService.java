package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.util.Date;


public class FareCalculatorService {

    public void calculateFare(Ticket ticket){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            assert ticket.getOutTime() != null;
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime(); // Date in parking
        Date outHour = ticket.getOutTime(); // Date out parking

        long inHourPark = inHour.getTime(); // convert inHour in milliseconds
        long outHourPark = outHour.getTime(); // convert outHour in milliseconds

        double duration = outHourPark - inHourPark; // past time

        duration = duration/60/1000; // convert in minutes

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                  if (duration<60){
                    ticket.setPrice((Fare.CAR_RATE_PER_HOUR/60)*duration);
                  }
                ticket.setPrice((duration/60) * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                if (duration<60){
                    ticket.setPrice(duration * (Fare.BIKE_RATE_PER_HOUR/60));
                }
                ticket.setPrice((duration/60) * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}