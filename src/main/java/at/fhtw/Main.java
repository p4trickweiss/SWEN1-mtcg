package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mtcgapp.service.echo.EchoService;
import at.fhtw.mtcgapp.service.session.SessionsService;
import at.fhtw.mtcgapp.service.user.UserService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addService("/echo", new EchoService());
        router.addService("/users", new UserService());
        router.addService("/sessions",  new SessionsService());
        return router;
    }
}
