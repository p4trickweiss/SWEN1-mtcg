package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.model.Message;
import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.model.userview.MessageUserView;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageRepo {

    private final UOW uow;

    public MessageRepo(UOW uow) {
        this.uow = uow;
    }

    public List<MessageUserView> getMessagesByUsername(User user) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT sender, message FROM message WHERE recipient = ?")) {
            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<MessageUserView> messages = new ArrayList<>();

            while(resultSet.next()) {
                messages.add(new MessageUserView(
                        resultSet.getString(1),
                        resultSet.getString(2)
                ));
            }
            return messages;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getMessagesByUsername error");
        }
    }

    public void sendMessage(Message message) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO message VALUES (0, ?, ?, ?)")) {
            preparedStatement.setString(1, message.getSender());
            preparedStatement.setString(2, message.getRecipient());
            preparedStatement.setString(3, message.getMessage());
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("sendMessage error");
        }
    }
}
