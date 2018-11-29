package dao;

import model.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class contains all config of database and used for connecting db
 */
public class DBConnector {
    // The name of database
    private final static String dbname = "xe";

    // The username of database
    //private final static String username = "system";

    // The password to access database
    //private final static String password = "system";

    // The url to access database
    //private final static String url = "jdbc:oracle:thin:@//127.0.0.1:1521/" + dbname;

    private final static String username = "zherendong";
//
    private final static String password = "CS174Aproject";
//
    private final static String url = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:" + dbname;

    // Database driver using jdbc
    private final static String driver = "oracle.jdbc.driver.OracleDriver";

    // private static Connection connection = null;

    /**
     * This class returns a connection
     *
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Firstly load the driver
            Class.forName(driver);
            // Access database with url above and correct username & password
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return connection
        return connection;
    }

}
