package at.fhtw.mtcgapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.BattleLogRepo;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.LobbyRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.BattleLogEntry;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.model.userview.LogEntryUserView;
import com.fasterxml.jackson.core.JacksonException;

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
                return new Response(HttpStatus.OK,
                                    ContentType.PLAIN_TEXT,
                                    "joined lobby, bid: " + bid
                );
            }

            else {
                User userTwo = userRepo.getUserByUsername(usernameUserTwo);
                int bid = lobbyRepo.removePlayerFromLobby(userTwo);

                CardRepo cardRepo = new CardRepo(uow);
                BattleLogRepo battleLogRepo = new BattleLogRepo(uow);

                List<Card> deckUserOne = cardRepo.getCardsInDeckUser(userOne);
                List<Card> deckUserTwo = cardRepo.getCardsInDeckUser(userTwo);

                int battleWinner = 0;
                Random rand = new Random();
                for(int roundCounter = 1; roundCounter <= 100; roundCounter++) {
                    if(deckUserOne.isEmpty() || deckUserTwo.isEmpty()) {
                        if(deckUserOne.isEmpty()) {
                            battleWinner = 2;
                        }
                        else {
                            battleWinner = 1;
                        }
                        break;
                    }
                    
                    Card cardUserOne = deckUserOne.get(rand.nextInt(deckUserOne.size()));
                    Card cardUserTwo = deckUserTwo.get(rand.nextInt(deckUserTwo.size()));
                    int winner;

                    if(cardUserOne.getType().equals("monster") && cardUserTwo.getType().equals("monster")) {
                        winner = this.pureMonsterFight(cardUserOne, cardUserTwo);

                    }
                    else if(cardUserOne.getType().equals("spell") && cardUserTwo.getType().equals("spell")) {
                        winner = this.spellFight(cardUserOne, cardUserTwo);

                    }
                    else {
                        winner = this.spellFight(cardUserOne, cardUserTwo);

                    }

                    if(winner == 1) {
                        deckUserTwo.remove(cardUserTwo);
                        deckUserOne.add(cardUserTwo);
                    }
                    if(winner == 2) {
                        deckUserOne.remove(cardUserOne);
                        deckUserTwo.add(cardUserOne);
                    }
                    BattleLogEntry round = new BattleLogEntry(bid, userOne.getUsername(), userTwo.getUsername(), cardUserOne.getName(), cardUserTwo.getName(), cardUserOne.getDamage(), cardUserTwo.getDamage(), false);
                    battleLogRepo.addLogEntry(round);
                }

                BattleLogEntry battleLogEntry = new BattleLogEntry(bid, userOne.getUsername(), userTwo.getUsername(), " ", " ", 10, 10, true);
                battleLogRepo.addLogEntry(battleLogEntry);

                if(battleWinner == 1) {
                    userRepo.updateEloWin(userOne);
                    userRepo.updateWins(userOne);
                    userRepo.updateEloLoss(userTwo);
                    userRepo.updateLosses(userTwo);

                    cardRepo.clearDeck(userTwo);
                    for(Card card : deckUserOne) {
                        cardRepo.updateCardOwnerById(userOne.getId(), card.getCid());
                    }
                }
                else if(battleWinner == 2) {
                    userRepo.updateEloWin(userTwo);
                    userRepo.updateWins(userTwo);
                    userRepo.updateEloLoss(userOne);
                    userRepo.updateLosses(userOne);

                    cardRepo.clearDeck(userOne);
                    for(Card card : deckUserTwo) {
                        cardRepo.updateCardOwnerById(userTwo.getId(), card.getCid());
                    }
                }

                List<LogEntryUserView> logEntries = battleLogRepo.getLogEntriesUserViewById(bid);
                String json = null;
                try {
                    json = this.getObjectMapper().writeValueAsString(logEntries);
                } catch (JacksonException e) {
                    e.printStackTrace();
                }
                uow.commitTransaction();
                return new Response(HttpStatus.OK,
                        ContentType.JSON,
                        json
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
        if(this.specialRules(cardOne, cardTwo) != -1) {
            return this.specialRules(cardOne, cardTwo);
        }

        int damageCardOne = cardOne.getDamage();
        int damageCardTwo = cardTwo.getDamage();
        return returnWinner(damageCardOne, damageCardTwo);
    }

    public int spellFight(Card cardOne, Card cardTwo) {
        if(this.specialRules(cardOne, cardTwo) != -1) {
            return this.specialRules(cardOne, cardTwo);
        }

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

    public int specialRules(Card cardOne, Card cardTwo) {
        String cardOneName = cardOne.getName().toLowerCase();
        String cardTwoName = cardTwo.getName().toLowerCase();

        if(cardOneName.contains("goblin") && cardTwoName.contains("dragon")) {
            return 1;
        }
        else if(cardOneName.contains("dragon") && cardTwoName.contains("goblin")) {
            return 2;
        }
        else if(cardOneName.contains("wizard") && cardTwoName.contains("ork")) {
            return 1;
        }
        else if(cardOneName.contains("ork") && cardTwoName.contains("wizard")) {
            return 2;
        }
        else if(cardOneName.contains("knight") && cardTwoName.contains("waterspell")) {
            return 2;
        }
        else if(cardOneName.contains("waterspell") && cardTwoName.contains("knight")) {
            return 1;
        }
        else if(cardOneName.contains("kraken") && cardTwo.getType().equals("spell")) {
            return 1;
        }
        else if(cardOne.getType().equals("spell") && cardTwoName.contains("kraken")) {
            return 2;
        }
        else if(cardOneName.contains("fireelve") && cardTwoName.contains("dragon")) {
            return 1;
        }
        else if(cardOneName.contains("dragon") && cardTwoName.contains("fireelve")) {
            return 2;
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
