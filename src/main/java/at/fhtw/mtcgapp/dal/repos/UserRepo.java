package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepo {

    private Connection connection;
    private List<User> userData;

    public UserRepo(Connection connection) { this.connection = connection;}

    public User getUser(int id) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                 user = new User(resultSet.getInt(1),
                         resultSet.getString(2),
                         resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUser(String username) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\" WHERE username = ?")){
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                user = new User(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void createUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO \"user\" (username, password) VALUES (?, ?)")) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int checkUserSession(User user) {
        int tokenCounter = 0;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM  \"user\" AS u JOIN \"sessions\" AS s ON u.uid = s.fk_uid WHERE username = ?")) {
            statement.setString(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                tokenCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokenCounter;
    }

    public String getToken(User user) {
        String token = "";
        try (PreparedStatement statement = connection.prepareStatement("SELECT token FROM  \"user\" AS u JOIN \"sessions\" AS s ON u.uid = s.fk_uid WHERE username = ?")) {
            statement.setString(1, user.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                token = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public void createSession(User user) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO sessions (token, fk_uid) VALUES (?, (SELECT uid FROM \"user\" WHERE username = ?))")) {
            statement.setString(1, user.getToken());
            statement.setString(2, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
