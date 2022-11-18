package at.fhtw.mtcgapp.dal;

import java.sql.Connection;
import java.sql.DriverManager;

public enum Singleton {

    INSTANCE;

    private Connection connection;

    Singleton() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mtcg_db", "swe1user", "swe1pw");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
