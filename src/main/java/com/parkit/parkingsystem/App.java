package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * parking space management application with invoicing
 *
 * @author Eric
 * @version 1.0
 */
public class App {

    private static final Logger logger = LogManager.getLogger("App");

    public static void main(String[] args) {
        PropertyConfigurator.configure("resources/log4j.properties");//Path file log4j.properties
        logger.setLevel(Level.DEBUG); //Level log DEBUG
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
