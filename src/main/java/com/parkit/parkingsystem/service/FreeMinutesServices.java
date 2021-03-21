package com.parkit.parkingsystem.service;

public class FreeMinutesServices {

    public static double FreeMinutes(double nbrFreeMinutes) {
        nbrFreeMinutes *= 60000; //convert milliseconds
        return nbrFreeMinutes;
    }
}
