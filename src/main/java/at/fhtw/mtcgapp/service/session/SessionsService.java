package at.fhtw.mtcgapp.service.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class SessionsService implements Service {

    private final SessionsController sessionsController;

    public SessionsService() { this.sessionsController = new SessionsController(); }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST) {
            return this.sessionsController.logIn(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
