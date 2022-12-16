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

public class TradingRepo {

    private final UOW uow;

    public TradingRepo(UOW uow) {
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
                        resultSet.getString(4),
                        resultSet.getInt(3))
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
}
