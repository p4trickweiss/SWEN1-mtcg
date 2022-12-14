package at.fhtw.mtcgapp.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.userview.CardUserView;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JacksonException;

import java.util.ArrayList;
import java.util.List;

public class DeckController extends Controller {

    public DeckController() {
    }

    public Response getDeck(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();

        try {
            UserRepo userRepo = new UserRepo(uow);
            CardRepo cardRepo = new CardRepo(uow);

            User user = userRepo.getUserByToken(token);
            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            List<CardUserView> cards = cardRepo.getCardsInDeckUserView(user);
            if(cards.isEmpty()) {
                uow.commitTransaction();
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\" : \"The request was fine, but the deck doesn't have any cards\" }"
                );
            }

            String json = null;
            try {
                json = this.getObjectMapper().writeValueAsString(cards);
            } catch (JacksonException e) {
                e.printStackTrace();
            }
            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    json
            );
        } catch (DataAccessException dataAccessException) {
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

    public Response updateDeck(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();

        try {
            UserRepo userRepo = new UserRepo(uow);
            CardRepo cardRepo = new CardRepo(uow);

            User user = userRepo.getUserByToken(token);
            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            List<String> list = new ArrayList<>();
            try {
                list = this.getObjectMapper().readValue(request.getBody(), ArrayList.class);
            } catch (JacksonException e) {
                e.printStackTrace();
            }

            if(list.size() != 4) {
                uow.commitTransaction();
                return new Response(HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deck did not include the required amount of cards\" }"
                );
            }

            cardRepo.clearDeck(user);
            for (String card : list) {
                if(!cardRepo.checkCardBelongsToUser(user, card)) {
                    return new Response(HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"message\" : \"One of the provided cards does not belong to the user\" }"
                    );
                }
                cardRepo.putCardInDeck(card, user);
            }
            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"The deck has been successfully configured\" }"
            );

        } catch (DataAccessException dataAccessException) {
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
}
