package at.fhtw.mtcgapp.dal;

import java.sql.Connection;

public class UOW {

    private final Connection connection;

    public UOW() {
        this.connection = DbSingleton.INSTANCE.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() {
        
    }
}

//https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
