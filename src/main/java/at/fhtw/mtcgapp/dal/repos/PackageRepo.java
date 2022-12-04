package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.CardInfoUser;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepo {

    private final Connection connection;

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

    public Package getPackage() throws SQLException {
        Package cardPackage = null;
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM package WHERE is_available = true");
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            cardPackage = new Package(
                    resultSet.getInt(1),
                    resultSet.getInt(2),
                    resultSet.getBoolean(3)
            );
        }
        return cardPackage;
    }

    public void makePackageUnavailable(Package cardPackage) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE package SET is_available = false WHERE pid = ?");
        statement.setInt(1, cardPackage.getPid());
        statement.execute();
    }

    public void acquireCardByPid(Package cardPackage, User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE card SET fk_uid = (SELECT uid FROM \"user\" WHERE username = ?) WHERE fk_pid = ?");
        statement.setString(1, user.getUsername());
        statement.setInt(2, cardPackage.getPid());
        statement.execute();
    }

    public List<CardInfoUser> getCardsByPid(Package cardPackage) throws SQLException {
        List<CardInfoUser> cards = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM card WHERE fk_pid = ?");
        statement.setInt(1, cardPackage.getPid());
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            cards.add(new CardInfoUser(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3)
            ));
        }
        return cards;
    }

    public List<CardInfoUser> getCardsByUid(User user) throws SQLException {
        List<CardInfoUser> cards = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM card WHERE fk_uid = ?");
        statement.setInt(1, user.getId());
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            cards.add(new CardInfoUser(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3)
            ));
        }
        return cards;
    }
}
