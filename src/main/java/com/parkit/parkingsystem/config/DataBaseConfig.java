package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * DataBaseConfig manages the connection to a database
 *
 * @author Eric
 * @version 1.0
 */
public class DataBaseConfig {
    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    /**
     * Get the connection jdbc mysql
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        String user = null;
        String pass = null;

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("resources/db.properties"));
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                user = key;
                pass = value;
            }
        } catch (IOException e) {
            logger.error("File load Error : " + e);
        }

        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod?useUnicode=true" +
                        "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&" +
                        "serverTimezone=UTC", user, pass);
    }

    /**
     * Close the connection
     */
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection", e);
            }
        }
    }


    /**
     * Close the preparedStatement
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement", e);
            }
        }
    }

    /**
     * Close the result set
     */
    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set", e);
            }
        }
    }
}
