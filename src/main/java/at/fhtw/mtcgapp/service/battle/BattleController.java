package at.fhtw.mtcgapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.LobbyRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.User;

import java.util.List;
import java.util.Random;


public class BattleController extends Controller {

    public Response battle(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            UserRepo userRepo = new UserRepo(uow);

            User userOne = userRepo.getUserByToken(token);
            if(userOne == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            LobbyRepo lobbyRepo = new LobbyRepo(uow);
            String usernameUserTwo = lobbyRepo.getPlayerFromLobby();
            if(usernameUserTwo == null) {
                lobbyRepo.joinLobby(userOne);
            }
            else {
                User userTwo = userRepo.getUserByUsername(usernameUserTwo);
                lobbyRepo.removePlayerFromLobby(userTwo);
                CardRepo cardRepo = new CardRepo(uow);

                List<Card> deckUserOne = cardRepo.getCardsInDeckUser(userOne);
                List<Card> deckUserTwo = cardRepo.getCardsInDeckUser(userTwo);

                Random rand = new Random();
                for(int roundCounter = 0; roundCounter < 100; roundCounter++) {
                    Card cardUserOne = deckUserOne.get(rand.nextInt(deckUserOne.size()));
                    Card cardUserTwo = deckUserTwo.get(rand.nextInt(deckUserTwo.size()));

                    if(cardUserOne.getType().equals("monster") && cardUserTwo.getType().equals("monster")) {
                        this.pureMonsterFight(cardUserOne, cardUserTwo);
                    }
                    if(cardUserOne.getType().equals("spell") && cardUserTwo.getType().equals("spell")) {
                        this.pureSpellFight(cardUserOne, cardUserTwo);
                    }
                    else {
                        this.mixedFight(cardUserOne, cardUserTwo);
                    }
                }


            }
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

    public void pureMonsterFight(Card cardOne, Card cardTwo) {

    }

    public void pureSpellFight(Card cardOne, Card cardTwo) {

    }

    public void mixedFight(Card cardOne, Card cardTwo) {

    }
}
