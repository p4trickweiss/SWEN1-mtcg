package at.fhtw.mtcgapp.service.messages;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.dal.repos.MessageRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.Message;
import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.model.userview.MessageUserView;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class MessageController extends Controller {

    public Response getMessages(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            UserRepo userRepo = new UserRepo(uow);
            User user = userRepo.getUserByToken(token);

            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            MessageRepo messageRepo = new MessageRepo(uow);
            List<MessageUserView> messages = messageRepo.getMessagesByUsername(user);
            String json = null;
            try {
                json = this.getObjectMapper().writeValueAsString(messages);
            } catch (JacksonException e) {
                e.printStackTrace();
            }
            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    json
            );
        }
        catch (DataAccessException dataAccessException) {
            uow.rollbackTransaction();
        }
        finally {
            uow.finishWork();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    public Response sendMessage(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            Message message = this.getObjectMapper().readValue(request.getBody(), new TypeReference<>() {});
            try {
                UserRepo userRepo = new UserRepo(uow);
                User user = userRepo.getUserByToken(token);

                if(user == null) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Authentication information is missing or invalid\" }"
                    );
                }

                MessageRepo messageRepo = new MessageRepo(uow);
                message.setSender(user.getUsername());
                messageRepo.sendMessage(message);
                uow.commitTransaction();
                return new Response(HttpStatus.OK,
                        ContentType.JSON,
                        "{ message: \"Message sucessfully sent\" }"
                );
            }
            catch (DataAccessException dataAccessException) {
                uow.rollbackTransaction();
            }
            finally {
                uow.finishWork();
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
