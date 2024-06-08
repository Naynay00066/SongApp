package repository.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/appprojectdb";
        String username = "root";
        String password = "Cabbagesoup_2548";
        Connection connect = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connect;
    }
}