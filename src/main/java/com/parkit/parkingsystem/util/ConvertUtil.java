package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;

public class ConvertUtil {

    public static double convertFreeMinutes(Ticket ticket) {

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            assert ticket.getOutTime() != null;
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        Date inHour = ticket.getInTime(); // date in parking
        Date outHour = ticket.getOutTime(); // date out parking

        long inHourPark = inHour.getTime(); // convert inHour in milliseconds
        long outHourPark = outHour.getTime(); // convert outHour in milliseconds

        ticket.setFreeminute(30);
        double nbrFreeMinutes = ticket.getFreeminute(); // number free minutes
        nbrFreeMinutes *= 60000; // convert free minute to milliseconds

        //Calcul past time
        double duration = outHourPark - inHourPark; // past time

        if (duration<30){
            duration = 0;
        }

        duration -= nbrFreeMinutes; // subtract free minute from time spent
        duration = duration / 60 / 1000; // Convert to minutes
        return duration;
    }


}
