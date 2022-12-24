package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LobbyRepo {

    private final UOW uow;

    public LobbyRepo(UOW uow) {
        this.uow = uow;
    }

    public String getPlayerFromLobby() throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM lobby")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            String player = null;
            while(resultSet.next()) {
                player = resultSet.getString(2);
            }
            return player;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getPlayerFromLobbyError");
        }
    }

    public int joinLobby(User user) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO lobby VALUES (0, ?) RETURNING bid")) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            int bid = 0;
            while(resultSet.next()) {
                bid = resultSet.getInt(1);
            }
            return bid;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("joinLobby error");
        }
    }

    public int removePlayerFromLobby(User user) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("DELETE FROM lobby WHERE player = ? RETURNING bid")) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            int bid = 0;
            while(resultSet.next()) {
                bid = resultSet.getInt(1);
            }
            return bid;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("removePlayerFromLobby error");
        }
    }
}
