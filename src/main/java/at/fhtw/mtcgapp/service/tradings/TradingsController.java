package at.fhtw.mtcgapp.service.tradings;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.StoreRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.userview.CardUserView;
import at.fhtw.mtcgapp.model.TradingDeal;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JacksonException;

import java.util.List;

public class TradingsController extends Controller {

    public Response getTradingDeals(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            UserRepo userRepo = new UserRepo(uow);

            User user = userRepo.getUserByToken(token);
            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            StoreRepo tradingRepo = new StoreRepo(uow);
            List<TradingDeal> deals = tradingRepo.getTradingDeals();
            if(deals.isEmpty()) {
                uow.commitTransaction();
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\" : \"The request was fine, but the are no trading deals available\" }"
                );
            }

            String json = null;
            try {
                json = this.getObjectMapper().writeValueAsString(deals);
            } catch (JacksonException e) {
                e.printStackTrace();
            }
            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    json
            );
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

    public Response createTradingDeal(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            TradingDeal tradingDeal = this.getObjectMapper().readValue(request.getBody(), TradingDeal.class);
            try {
                UserRepo userRepo = new UserRepo(uow);

                User user = userRepo.getUserByToken(token);
                if (user == null) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Authentication information is missing or invalid\" }"
                    );
                }

                CardRepo cardRepo = new CardRepo(uow);
                List<CardUserView> userCards = cardRepo.getCardsByUid(user);
                for (CardUserView card : userCards) {
                    if(card.getCid().equals(tradingDeal.getCardToTrade()) && cardRepo.checkCardIsEnabledToTrade(tradingDeal.getCardToTrade())) {
                        StoreRepo tradingRepo = new StoreRepo(uow);
                        cardRepo.lockCard(tradingDeal.getCardToTrade());
                        tradingRepo.createTradingDeal(user, tradingDeal);
                        uow.commitTransaction();
                        return new Response(HttpStatus.OK,
                                ContentType.JSON,
                                "{ \"message\" : \"Trading successfully created\" }"
                        );
                    }
                }
                uow.commitTransaction();
                return new Response(HttpStatus.FORBIDDEN,
                                    ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is not owned by the user or locked in the deck.\" }"
                );
            } catch (DataAccessException dataAccessException) {
                uow.rollbackTransaction();
                if(dataAccessException.getMessage().equals("A deal with this deal ID already exists")) {
                    return new Response(HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"message\" : \"A deal with this deal ID already exists\" }"
                    );
                }
            } finally {
                uow.finishWork();
            }
        }
        catch (JacksonException e) {
            e.printStackTrace();
        }


        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{\"message\" : \"Internal Server Error\" }"
        );
    }

    public Response deleteTradingDeal(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();
        try {
            UserRepo userRepo = new UserRepo(uow);
            User user = userRepo.getUserByToken(token);

            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            String dealToDelete = request.getPathParts().get(1);
            StoreRepo tradingRepo = new StoreRepo(uow);
            TradingDeal tradingDeal = tradingRepo.getTradingDealById(dealToDelete);
            if(tradingDeal == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deal ID was not found.\" }"
                );
            }

            if(!tradingRepo.checkCardBelongsToUser(user, tradingDeal)) {
                uow.commitTransaction();
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The deal contains a card that is not owned by the user.\" }"
                );
            }

            tradingRepo.deleteTradingDealById(tradingDeal.getId());
            tradingRepo.unlockCardById(tradingDeal.getCardToTrade());
            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"Trading deal successfully deleted\" }"
            );
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

    public Response trade(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();
        String cardInRequest = request.getBody().replace("\"", "");

        try {
            UserRepo userRepo = new UserRepo(uow);

            User user = userRepo.getUserByToken(token);
            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            String dealToTrade = request.getPathParts().get(1);
            StoreRepo tradingRepo = new StoreRepo(uow);
            TradingDeal tradingDeal = tradingRepo.getTradingDealById(dealToTrade);
            if(tradingDeal == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"The provided deal ID was not found.\" }"
                );
            }

            CardRepo cardRepo = new CardRepo(uow);
            Card cardToTrade = cardRepo.getCardByIdFromUser(user, cardInRequest);
            if(cardToTrade == null ||
                    cardToTrade.isIn_deck() ||
                    cardToTrade.isIs_locked() ||
                    (cardToTrade.getDamage() < tradingDeal.getMinimumDamage()) ||
                    //!cardToTrade.getType().equals(tradingDeal.getType()) ||
                    tradingDeal.getFkUid() == user.getId()) {
                uow.commitTransaction();
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck, or you tried to trade with yourself.\" }"
                );
            }

            System.out.println(tradingDeal.getFkUid());
            User provider = userRepo.getUserById(tradingDeal.getFkUid());
            System.out.println(provider.toString());
            cardRepo.updateCardOwnerById(provider.getId(), tradingDeal.getCardToTrade());
            tradingRepo.unlockCardById(tradingDeal.getCardToTrade());
            cardRepo.updateCardOwnerById(user.getId(), cardToTrade.getCid());
            tradingRepo.unlockCardById(cardToTrade.getCid());
            tradingRepo.deleteTradingDealById(tradingDeal.getId());

            uow.commitTransaction();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\" : \"Card successfully traded.\" }"
            );
        }
        catch (DataAccessException dataAccessException) {
            System.out.println("error");
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
