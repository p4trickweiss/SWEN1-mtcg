package at.fhtw.mtcgapp.dal;

import java.sql.Connection;
import java.sql.DriverManager;

public enum DbSingleton {

    INSTANCE;

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mtcg_db",
                    "swe1user",
                    "swe1pw");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }
}
