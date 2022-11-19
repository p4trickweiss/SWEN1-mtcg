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

    public User getUserById(int id) {
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

    public User getUserByUsername(String username) {
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

    public User getUserByToken(String token) {
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"user\" WHERE token = ?")){
            statement.setString(1, token);
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

    public void setToken(User user) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE \"user\" SET token = ? WHERE username = ?")) {
            statement.setString(1, user.getToken());
            statement.setString(2, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getToken(User user) {
        String token = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT token FROM  \"user\" WHERE username = ?")) {
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

    public void updateUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE \"user\" SET name = ?, bio = ?, image = ? WHERE username = ?")) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getBio());
            statement.setString(3, user.getImage());
            statement.setString(4, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
