package at.fhtw.mtcgapp.dal;

import java.sql.Connection;

public class UOW {

    private final Connection connection;

    public UOW() {
        this.connection = Singleton.INSTANCE.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }
}

//https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
