package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.mtcgapp.service.cards.CardsController;
import at.fhtw.mtcgapp.service.cards.CardsService;
import at.fhtw.mtcgapp.service.deck.DeckController;
import at.fhtw.mtcgapp.service.deck.DeckService;
import at.fhtw.mtcgapp.service.echo.EchoService;
import at.fhtw.mtcgapp.service.packages.PackagesController;
import at.fhtw.mtcgapp.service.packages.PackagesService;
import at.fhtw.mtcgapp.service.sessions.SessionsController;
import at.fhtw.mtcgapp.service.sessions.SessionsService;
import at.fhtw.mtcgapp.service.transactions.TransactionsController;
import at.fhtw.mtcgapp.service.transactions.TransactionsService;
import at.fhtw.mtcgapp.service.user.UserController;
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
        router.addService("/users", new UserService(new UserController()));
        router.addService("/sessions",  new SessionsService(new SessionsController()));
        router.addService("/packages", new PackagesService(new PackagesController()));
        router.addService("/transactions", new TransactionsService(new TransactionsController()));
        router.addService("/cards", new CardsService(new CardsController()));
        router.addService("/deck", new DeckService(new DeckController()));
        return router;
    }
}
