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

    public UserController() {}

    public Response addUser(Request request) {
        UOW uow = new UOW();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                UserRepo userRepo = new UserRepo(uow.getConnection());
                uow.getConnection().setAutoCommit(false);

                userRepo.createUser(user);
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ message : \"Success\" }"
                );

            } catch (SQLException sqlException) {
                if (uow.getConnection() != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        uow.getConnection().rollback();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(sqlException.getErrorCode() == 0) {
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ message : \"User already exists\" }"
                    );
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

    public Response getUser(Request request) {
        String token = request.getToken();
        String userToSearch = request.getPathParts().get(1);
        UOW uow = new UOW();
        try {
            UserRepo userRepo = new UserRepo(uow.getConnection());
            uow.getConnection().setAutoCommit(false);

            User user = userRepo.getUserByUsername(userToSearch);
            if(user == null) {
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\" : \"User not found\" }"
                );
            }

            if(token.equals("admin-mtcgToken") || token.equals(user.getToken())) {
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"Name\" : \"" + user.getName() +
                                "\", \"Bio\" : \"" + user.getBio() +
                                "\", \"Image\" : \"" + user.getImage() + "\" }"
                );
            }
            uow.getConnection().commit();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{\"message\" : \"Authentication information is missing or invalid\" }"
            );
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{\"message\" : \"Internal Server Error\" }"
        );
    }

    public Response updateUser(Request request) {
        String token = request.getToken();
        User userToModify = new User();
        userToModify.setUsername(request.getPathParts().get(1));
        UOW uow = new UOW();
        try {
            User userData = this.getObjectMapper().readValue(request.getBody(), User.class);
            try{
                UserRepo userRepo = new UserRepo(uow.getConnection());
                uow.getConnection().setAutoCommit(false);

                if(userRepo.getUserByUsername(userToModify.getUsername()) == null) {
                    uow.getConnection().commit();
                    return new Response(
                            HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{\"message\" : \"User not found\" }"
                    );
                }

                userToModify.setToken(userRepo.getToken(userToModify));
                if(token.equals("admin-mtcgToken") || token.equals(userToModify.getToken())) {
                    userData.setUsername(userToModify.getUsername());
                    userRepo.updateUser(userData);
                    uow.getConnection().commit();
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{\"message\" : \"User updated\" }"
                    );
                }
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\" : \"Authentication information is missing or invalid\" }"
                );
            } catch (SQLException sqlException) {
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
                "{\"message\" : \"Internal Server Error\" }"
        );
    }
}
