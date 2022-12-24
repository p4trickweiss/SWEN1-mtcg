package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.BattleLogEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BattleLogRepo {

    private final UOW uow;

    public BattleLogRepo(UOW uow) {
        this.uow = uow;
    }

    public boolean checkFinishedByBid(int bid) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT finished FROM \"battle-log\" WHERE bid = ? and finished = true")) {
            preparedStatement.setInt(1, bid);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean finished = false;
            while (resultSet.next()) {
                finished = resultSet.getBoolean(1);
            }
            return finished;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("checkFinishedByBid error");
        }
    }

    public void addLogEntry(BattleLogEntry battleLogEntry) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO \"battle-log\" VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, battleLogEntry.getBid());
            preparedStatement.setString(2, battleLogEntry.getPlayerA());
            preparedStatement.setString(3, battleLogEntry.getPlayerB());
            preparedStatement.setString(4, battleLogEntry.getCardPlayerA());
            preparedStatement.setString(5, battleLogEntry.getCardPlayerB());
            preparedStatement.setInt(6, battleLogEntry.getDamageCardA());
            preparedStatement.setInt(7, battleLogEntry.getDamageCardB());
            preparedStatement.setBoolean(8, battleLogEntry.isFinished());
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("addLogEntry error");
        }
    }
}
