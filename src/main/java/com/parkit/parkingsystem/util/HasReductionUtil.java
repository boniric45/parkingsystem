package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.dao.TicketDAO;

//droit a la reduction
public class HasReductionUtil {
    public static boolean hasReduction(String vehiculeRegNumber) {

        boolean reduction;
        TicketDAO ticketDAO = new TicketDAO();
        int numberTicketFind = ticketDAO.getTicketDatabase(vehiculeRegNumber);

        //Fonction réduction
        if (numberTicketFind >= 3) {
            reduction = true;
        } else {
            reduction = false;
        }
        return reduction;
    }
}
