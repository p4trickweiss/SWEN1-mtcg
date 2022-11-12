package at.fhtw.mtcgapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public class UserController extends Controller {

    private UserUOW userUOW;

    public UserController() {
        this.userUOW = new UserUOW();
    }

    public Response addUser(Request request) {

        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                this.userUOW.addUser(user);
            } catch (Exception e) {
                if(e.getMessage() == "User already exists")
                    return new Response(
                            HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ message : \"User already exists\" }"
                    );
            }

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ message : \"Success\" }"
                    );
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
