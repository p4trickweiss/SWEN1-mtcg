package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.TradingDeal;
import at.fhtw.mtcgapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreRepo {

    private final UOW uow;

    public StoreRepo(UOW uow) {
        this.uow = uow;
    }

    public List<TradingDeal> getTradingDeals() throws DataAccessException {
        List<TradingDeal> deals = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM store")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                deals.add(new TradingDeal(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getInt(5))
                );
            }
            return deals;
        }
        catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
            throw new DataAccessException("getTradingDeals error");
        }
    }

    public void createTradingDeal(User user, TradingDeal tradingDeal) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO store VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, tradingDeal.getId());
            preparedStatement.setString(2, tradingDeal.getCardToTrade());
            preparedStatement.setInt(3, tradingDeal.getMinimumDamage());
            preparedStatement.setString(4, tradingDeal.getType());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            if(sqlException.getErrorCode() == 0) {
                throw new DataAccessException("A deal with this deal ID already exists");
            }
            throw new DataAccessException("createTradingDeal error");
        }
    }

    public TradingDeal getTradingDealById(String id) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM store WHERE tid = ?")) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            TradingDeal tradingDeal = null;
            while(resultSet.next()) {
                tradingDeal = new TradingDeal(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getInt(5)
                );
            }
            return tradingDeal;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getTradingDealById error");
        }
    }

    public boolean checkCardBelongsToUser(User user, TradingDeal tradingDeal) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE cid = ? and fk_uid = ?")) {
            preparedStatement.setString(1, tradingDeal.getCardToTrade());
            preparedStatement.setInt(2, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean cardBelongsToUser = false;
            while(resultSet.next()) {
                cardBelongsToUser = true;
            }
            return cardBelongsToUser;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("checkCardBelongsToUser error");
        }
    }

    public void deleteTradingDealById(String id) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("DELETE FROM store WHERE tid = ?")) {
            preparedStatement.setString(1, id);
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("deleteTradingDealById error");
        }
    }

    public void unlockCardById(String id) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET is_locked = false WHERE cid = ?")) {
            preparedStatement.setString(1, id);
            preparedStatement.execute();
        }
        catch(SQLException sqlException) {
            throw new DataAccessException("unlockCardById error");
        }
    }
}
