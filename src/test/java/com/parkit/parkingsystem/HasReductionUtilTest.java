package com.parkit.parkingsystem;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import org.junit.jupiter.api.BeforeAll;

public class HasReductionUtilTest {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;

    @BeforeAll
    private static void setUp() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    public static boolean hasReduction(String vehiculeRegNumber) {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        boolean reduction;
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
