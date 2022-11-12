package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.service.user.UserUOW;

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
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");){
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
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\" WHERE username = ?");){
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
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO \"user\" (username, password) VALUES (?, ?)");) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
