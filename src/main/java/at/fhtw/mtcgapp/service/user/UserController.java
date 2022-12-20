package at.fhtw.mtcgapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.model.userview.UserDataUserView;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserController extends Controller {

    public UserController() {}

    public Response addUser(Request request) {
        UOW uow = new UOW();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            UserRepo userRepo = new UserRepo(uow);
            try {
                userRepo.createUser(user);
                uow.commitTransaction();
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ message : \"Success\" }"
                );
            }
            catch(DataAccessException dataAccessException) {
                uow.rollbackTransaction();
                if(dataAccessException.getMessage().equals("User already exists")) {
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ message : \"User already exists\" }"
                    );
                }
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

    public Response getUser(Request request) {
        String token = request.getToken();
        String userToSearch = request.getPathParts().get(1);
        UOW uow = new UOW();

        try {
            UserRepo userRepo = new UserRepo(uow);
            User user = userRepo.getUserByUsername(userToSearch);
            if(user == null) {
                uow.commitTransaction();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{\"message\" : \"User not found\" }"
                );
            }

            if(token.equals("admin-mtcgToken") || token.equals(user.getToken())) {
                UserDataUserView userData = new UserDataUserView(user.getName(), user.getBio(), user.getImage());
                String json = null;
                try {
                    json = this.getObjectMapper().writeValueAsString(userData);
                } catch (JacksonException e) {
                    e.printStackTrace();
                }
                uow.commitTransaction();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        json
                );
            }

            uow.commitTransaction();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{\"message\" : \"Authentication information is missing or invalid\" }"
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
                UserRepo userRepo = new UserRepo(uow);

                if(userRepo.getUserByUsername(userToModify.getUsername()) == null) {
                    uow.commitTransaction();
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
                    uow.commitTransaction();
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{\"message\" : \"User updated\" }"
                    );
                }
                uow.commitTransaction();
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{\"message\" : \"Authentication information is missing or invalid\" }"
                );
            } catch (DataAccessException dataAccessException) {
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
                "{\"message\" : \"Internal Server Error\" }"
        );
    }
}
