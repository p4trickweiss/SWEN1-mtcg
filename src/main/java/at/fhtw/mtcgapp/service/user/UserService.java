package at.fhtw.mtcgapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class UserService implements Service {

    private final UserController userController;

    public UserService(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST) {
            return this.userController.addUser(request);
        } else if(request.getMethod() == Method.GET && request.getPathParts().size() > 1) {
            return this.userController.getUser(request);
        } else if(request.getMethod() == Method.PUT && request.getPathParts().size() > 1) {
            return this.userController.updateUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
