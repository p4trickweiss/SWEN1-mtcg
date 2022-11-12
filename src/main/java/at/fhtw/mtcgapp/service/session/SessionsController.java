package at.fhtw.mtcgapp.service.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SessionsController extends Controller {

    private final SessionsUOW sessionsUOW;

    public SessionsController() { sessionsUOW = new SessionsUOW(); }

    public Response logIn(Request request) {
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            try {
                this.sessionsUOW.logIn(user);
            } catch (Exception e) {
                if(e.getMessage() == "Invalid username/password provided")
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message : \"Invalid username/password provided\" }"
                    );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    user.getToken()
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
