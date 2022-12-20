package at.fhtw.mtcgapp.service.sessions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SessionsController extends Controller {

    public SessionsController() {}

    public Response logIn(Request request) {
        UOW uow = new UOW();

        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                UserRepo userRepo = new UserRepo(uow);
                User userCredentials = userRepo.getUserByUsername(user.getUsername());
                if(userCredentials != null && user.getUsername().equals(userCredentials.getUsername()) && user.getPassword().equals(userCredentials.getPassword())){
                    user.setToken(user.getUsername() + "-mtcgToken");
                    userRepo.setToken(user);
                    user.setToken(userRepo.getToken(user));
                } else {
                    uow.commitTransaction();
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Invalid username/password\" }"
                    );
                }
                uow.commitTransaction();
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        user.getToken()
                );
            } catch (DataAccessException dataAccessException)  {
                uow.rollbackTransaction();
            }
            finally {
                uow.finishWork();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
