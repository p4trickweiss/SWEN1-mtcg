package at.fhtw.mtcgapp.service.cards;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class CardsService implements Service {

    private final CardsController cardsController;

    public CardsService(CardsController cardsController) {
        this.cardsController = cardsController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET) {
            return this.cardsController.getCards(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
