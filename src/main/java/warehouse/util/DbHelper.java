package warehouse.util;

import lombok.SneakyThrows;
import warehouse.MenuRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper {
    @SneakyThrows(SQLException.class)
    public static Connection createConnection() {
        String dbUrl = MenuRunner.DB_PROPERTIES.getProperty("db.url");
        String dbLogin = MenuRunner.DB_PROPERTIES.getProperty("db.login");
        String dbPassword = MenuRunner.DB_PROPERTIES.getProperty("db.password");

        Connection connection = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
        connection.setAutoCommit(false);
        return connection;
    }
}
