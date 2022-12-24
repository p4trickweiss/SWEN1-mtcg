package at.fhtw.mtcgapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.BattleLogRepo;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.LobbyRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.BattleLogEntry;
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
                int bid = lobbyRepo.joinLobby(userOne);
                uow.commitTransaction();
                BattleLogRepo battleLogRepo = new BattleLogRepo(uow);
                while(!battleLogRepo.checkFinishedByBid(bid));
                uow.commitTransaction();
                return new Response(HttpStatus.OK,
                                    ContentType.PLAIN_TEXT,
                                    "battle done"
                );
            }
            else {
                User userTwo = userRepo.getUserByUsername(usernameUserTwo);
                int bid = lobbyRepo.removePlayerFromLobby(userTwo);

                CardRepo cardRepo = new CardRepo(uow);
                BattleLogRepo battleLogRepo = new BattleLogRepo(uow);

                List<Card> deckUserOne = cardRepo.getCardsInDeckUser(userOne);
                List<Card> deckUserTwo = cardRepo.getCardsInDeckUser(userTwo);

                Random rand = new Random();
                for(int roundCounter = 0; roundCounter < 100; roundCounter++) {
                    if(deckUserOne.isEmpty() || deckUserTwo.isEmpty()) {
                        BattleLogEntry battleLogEntry = new BattleLogEntry(bid, userOne.getUsername(), userTwo.getUsername(), " ", " ", 10, 10, true);
                        uow.commitTransaction();
                        return new Response(HttpStatus.OK,
                                ContentType.PLAIN_TEXT,
                                "deck empty"
                        );
                    }
                    Card cardUserOne = deckUserOne.get(rand.nextInt(deckUserOne.size()));
                    Card cardUserTwo = deckUserTwo.get(rand.nextInt(deckUserTwo.size()));
                    int winner;

                    if(cardUserOne.getType().equals("monster") && cardUserTwo.getType().equals("monster")) {
                        winner = this.pureMonsterFight(cardUserOne, cardUserTwo);
                        if(winner == 1) {
                            deckUserTwo.remove(cardUserTwo);
                            deckUserOne.add(cardUserTwo);
                        }
                        if(winner == 2) {
                            deckUserOne.remove(cardUserOne);
                            deckUserTwo.add(cardUserOne);
                        }
                    }
                    else if(cardUserOne.getType().equals("spell") && cardUserTwo.getType().equals("spell")) {
                        winner = this.spellFight(cardUserOne, cardUserTwo);
                        if(winner == 1) {
                            deckUserTwo.remove(cardUserTwo);
                            deckUserOne.add(cardUserTwo);
                        }
                        if(winner == 2) {
                            deckUserOne.remove(cardUserOne);
                            deckUserTwo.add(cardUserOne);
                        }
                    }
                    else {
                        winner = this.spellFight(cardUserOne, cardUserTwo);
                        if(winner == 1) {
                            deckUserTwo.remove(cardUserTwo);
                            deckUserOne.add(cardUserTwo);
                        }
                        if(winner == 2) {
                            deckUserOne.remove(cardUserOne);
                            deckUserTwo.add(cardUserOne);
                        }
                    }
                    String roundWinner = winner == 1 ? userOne.getUsername() : userTwo.getUsername();
                    System.out.println("Round " + roundCounter + ": " + cardUserOne.getName() + " vs " + cardUserTwo.getName());
                    System.out.println("Round " + roundCounter + ": " + cardUserOne.getDamage() + " vs " + cardUserTwo.getDamage());
                    System.out.println("Winner of round " + roundCounter + ": " + roundWinner);
                    System.out.println("Deck of user1: " + deckUserOne.size() + " Deck of user2: " + deckUserTwo.size());
                    //battleLogRepo.
                }
                BattleLogEntry battleLogEntry = new BattleLogEntry(bid, userOne.getUsername(), userTwo.getUsername(), " ", " ", 10, 10, true);
                battleLogRepo.addLogEntry(battleLogEntry);
                uow.commitTransaction();
                return new Response(HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        "draw"
                );

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

    public int pureMonsterFight(Card cardOne, Card cardTwo) {
        int damageCardOne = cardOne.getDamage();
        int damageCardTwo = cardTwo.getDamage();
        return returnWinner(damageCardOne, damageCardTwo);
    }

    public int spellFight(Card cardOne, Card cardTwo) {
        int damageCardOne = cardOne.getDamage();
        int damageCardTwo = cardTwo.getDamage();

        if(cardOne.getElement().equals(cardTwo.getElement())) {
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("water") && cardTwo.getElement().equals("fire")) {
            damageCardOne = damageCardOne * 2;
            damageCardTwo =damageCardTwo / 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("fire") && cardTwo.getElement().equals("normal")) {
            damageCardOne = damageCardOne * 2;
            damageCardTwo =damageCardTwo / 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("normal") && cardTwo.getElement().equals("water")) {
            damageCardOne = damageCardOne * 2;
            damageCardTwo =damageCardTwo / 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("fire") && cardTwo.getElement().equals("water")) {
            damageCardOne = damageCardOne / 2;
            damageCardTwo =damageCardTwo * 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("normal") && cardTwo.getElement().equals("fire")) {
            damageCardOne = damageCardOne / 2;
            damageCardTwo =damageCardTwo * 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        else if(cardOne.getElement().equals("water") && cardTwo.getElement().equals("normal")) {
            damageCardOne = damageCardOne / 2;
            damageCardTwo =damageCardTwo * 2;
            return returnWinner(damageCardOne, damageCardTwo);
        }
        return -1;
    }

    public int returnWinner(int damageCardOne, int damageCardTwo) {
        if(damageCardOne == damageCardTwo) {
            return 0;
        }
        else if(damageCardOne > damageCardTwo){
            return 1;
        }
        else {
            return 2;
        }
    }
}
