package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.userview.StatsUserView;
import at.fhtw.mtcgapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    private final UOW uow;

    public UserRepo(UOW uow) {
        this.uow = uow;
    }

    public User getUserById(int id) throws DataAccessException {
        User user = null;
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM \"user\" WHERE uid = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getInt(8));
            }
            return user;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getUserByUsername error");
        }
    }

    public User getUserByUsername(String username) throws DataAccessException {
        User user = null;
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM \"user\" WHERE username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getInt(8));
            }
            return user;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getUserByUsername error");
        }
    }

    public User getUserByToken(String token) throws DataAccessException {
        User user = null;
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM \"user\" WHERE token = ?")) {
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getInt(8),
                        resultSet.getInt(9),
                        resultSet.getInt(10),
                        resultSet.getInt(11));
            }
            return user;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getUserByToken error");
        }
    }

    public void createUser(User user) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO \"user\" (username, password) VALUES (?, ?)")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            if(sqlException.getErrorCode() == 0) {
                throw new DataAccessException("User already exists");
            }
            throw new DataAccessException("createUser error");
        }
    }

    public void setToken(User user) throws DataAccessException{
            try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE \"user\" SET token = ? WHERE username = ?")) {
                preparedStatement.setString(1, user.getToken());
                preparedStatement.setString(2, user.getUsername());
                preparedStatement.execute();
            } catch (SQLException sqlException) {
                throw new DataAccessException("setToken error");
            }
        }

    public String getToken(User user) throws DataAccessException {
        String token = null;
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT token FROM  \"user\" WHERE username = ?")) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                token = resultSet.getString(1);
            }
            return token;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getToken error");
        }
    }

    public void updateUser(User user) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE \"user\" SET name = ?, bio = ?, image = ? WHERE username = ?")) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getBio());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new DataAccessException("updateUser error");
        }
    }

    public void payPackage(Package cardPackage, User user) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE \"user\" SET coins = coins - (SELECT price FROM package WHERE pid = ?) WHERE username = ?")) {
            preparedStatement.setInt(1, cardPackage.getPid());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new DataAccessException("payPackage error");
        }
    }

    public List<StatsUserView> getSortedScoreboard() throws DataAccessException {
        List<StatsUserView> userStats = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT name, elo, wins, losses FROM \"user\" ORDER BY elo DESC")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                userStats.add(new StatsUserView(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4))
                );
            }
            return userStats;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getUnsortedScoreboard error");
        }
    }
}
