package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.dao.TicketDAO;

/**
 * returns a boolean which indicates if the ticket is eligible for the reduction
 *
 * @author Eric
 * @version 1.0
 */

// Eligibility Reduction
public class HasReductionUtil {

    /**
     * returns a boolean which indicates if the ticket is eligible for the reduction
     */
    public static boolean hasReduction(String vehiculeRegNumber) {

        boolean reduction;
        TicketDAO ticketDAO = new TicketDAO();
        int numberTicketFind = ticketDAO.getTicketDatabase(vehiculeRegNumber);

        if (numberTicketFind >= 3) {
            reduction = true;
        } else {
            reduction = false;
        }
        return reduction;
    }
}
