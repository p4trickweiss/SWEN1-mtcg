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
                if(userRepo.getUserByUsername(user.getUsername()) == null) {
                    userRepo.createUser(user);
                    uow.getConnection().commit();
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.JSON,
                            "{ message : \"Success\" }"
                    );
                }
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ message : \"User already exists\" }"
                );
            } catch (Exception sqlException) {
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

    public Response getUser(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();
        String userToSearch = request.getPathParts().get(1);
        try {
            UserRepo userRepo = new UserRepo(uow.getConnection());
            uow.getConnection().setAutoCommit(false);
            if(userRepo.getUserByUsername(userToSearch) == null) {
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\" : \"User not found\" }"
                );
            }
            User userData = userRepo.getUserByUsername(userToSearch);
            if(token.equals("admin-mtcgToken") || token.equals(userData.getToken())) {
                uow.getConnection().commit();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"Name\" : \"" + userData.getName() +
                                "\", \"Bio\" : \"" + userData.getBio() +
                                "\", \"Image\" : \"" + userData.getImage() + "\" }"
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
        UOW uow = new UOW();
        String token = request.getHeaderMap().getHeader("Authorization");
        token = token.split(" ")[1];
        User userToModify = new User();
        userToModify.setUsername(request.getPathParts().get(1));
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
