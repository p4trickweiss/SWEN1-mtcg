package at.fhtw.mtcgapp.service.sessions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

public class SessionsController extends Controller {

    public SessionsController() {}

    public Response logIn(Request request) {
        UOW uow = new UOW();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                UserRepo userRepo = new UserRepo(uow.getConnection());
                uow.getConnection().setAutoCommit(false);
                User userdata = userRepo.getUserByUsername(user.getUsername());
                if(userdata != null && user.getUsername().equals(userdata.getUsername()) && user.getPassword().equals(userdata.getPassword())){
                    user.setToken(user.getUsername() + "-mtcgToken");
                    userRepo.setToken(user);
                    user.setToken(userRepo.getToken(user));
                } else {
                    uow.getConnection().commit();
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Invalid username/password\" }"
                    );
                }
               uow.getConnection().commit();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        user.getToken()
                );
            } catch (SQLException sqlException)  {
                sqlException.printStackTrace();
                if (uow.getConnection() != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        uow.getConnection().rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
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
