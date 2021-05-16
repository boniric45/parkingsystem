package com.parkit.parkingsystem.model;

import java.util.Date;

/**
 * Model ticket
 *
 * @author Eric
 * @version 1.0
 */
public class Ticket {

    private double freeMinute;
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public ParkingSpot getParkingSpot() {

        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {

        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {

        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {

        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        return (Date) inTime.clone();
    }

    public void setInTime(Date inTime) {
        this.inTime = (Date) inTime.clone();
    }

    public Date getOutTime() {
        return outTime != null
                ? new Date(outTime.getTime())
                : null;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime != null
                ? new Date(outTime.getTime())
                : null;
    }

    public double getFreeminute() {
        return freeMinute;
    }

    public void setFreeminute(double freeminute) {

        freeMinute = freeminute;
    }

}
