package at.fhtw.mtcgapp.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class DeckService implements Service {
    private DeckController deckController;

    public DeckService(DeckController deckController) {
        this.deckController = deckController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET) {
            return this.deckController.getDeck(request);
        }
        if(request.getMethod() == Method.PUT) {
            return this.deckController.updateDeck(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
