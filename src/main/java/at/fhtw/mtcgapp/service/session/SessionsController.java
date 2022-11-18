package at.fhtw.mtcgapp.service.session;

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

    private final UOW uow;

    public SessionsController() { uow = new UOW(); }

    public Response logIn(Request request) {
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                UserRepo userRepo = new UserRepo(this.uow.getConnection());
                this.uow.getConnection().setAutoCommit(false);
                User userdata = userRepo.getUser(user.getUsername());
                if(user.getUsername().equals(userdata.getUsername()) && user.getPassword().equals(userdata.getPassword())){
                    user.setToken(user.getUsername() + "-mtcgToken");
                    userRepo.createSession(user);
                } else {
                    this.uow.getConnection().commit();
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Invalid username/password\" }"
                    );
                }
               this.uow.getConnection().commit();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        user.getToken()
                );
            } catch (SQLException sqlException)  {
                sqlException.printStackTrace();
                if (this.uow.getConnection() != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        this.uow.getConnection().rollback();
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
