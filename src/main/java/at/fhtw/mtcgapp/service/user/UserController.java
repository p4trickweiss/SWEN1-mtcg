package at.fhtw.mtcgapp.service.user;

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

public class UserController extends Controller {

    private final UOW uow;

    public UserController() {
        this.uow = new UOW();
    }

    public Response addUser(Request request) {

        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                UserRepo userRepo = new UserRepo(this.uow.getConnection());
                this.uow.getConnection().setAutoCommit(false);
                if(userRepo.getUser(user.getUsername()) == null) {
                    userRepo.createUser(user);
                    this.uow.getConnection().commit();
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.JSON,
                            "{ message : \"Success\" }"
                    );
                }
                this.uow.getConnection().commit();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ message : \"User already exists\" }"
                );
            } catch (Exception sqlException) {
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
