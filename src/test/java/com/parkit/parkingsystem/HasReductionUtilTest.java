package com.parkit.parkingsystem;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareServiceTest;


public class HasReductionUtilTest {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    public static boolean hasReduction(String vehiculeRegNumber) {
        TicketDAO ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        DataBasePrepareServiceTest dataBasePrepareServiceTest = new DataBasePrepareServiceTest();
        boolean reduction;
        int numberTicketFind = ticketDAO.getTicketDatabase(vehiculeRegNumber) + 1;//Begin to 0
        reduction = numberTicketFind >= 3;
        return reduction;
    }
}
