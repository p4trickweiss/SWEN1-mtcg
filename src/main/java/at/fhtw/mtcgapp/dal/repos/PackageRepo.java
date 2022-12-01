package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepo {

    private Connection connection;

    public PackageRepo(Connection connection) {
        this.connection = connection;
    }

    public int createPackageAndGetId() throws SQLException{
        int pid = 0;
        PreparedStatement statement = connection.prepareStatement("INSERT INTO package DEFAULT VALUES RETURNING pid");
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            pid = resultSet.getInt(1);
        }
        return pid;
    }

    public void createCard(Card card) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO card (cid, name, damage, fk_pid) VALUES (?, ?, ?, ?)");
        statement.setString(1, card.getCid());
        statement.setString(2, card.getName());
        statement.setInt(3, card.getDamage());
        statement.setInt(4, card.getFk_pid());
        statement.execute();

    }
}
