package at.fhtw.mtcgapp.service.tradings;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.PackageRepo;
import at.fhtw.mtcgapp.dal.repos.TradingRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.CardInfoUser;
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

            TradingRepo tradingRepo = new TradingRepo(uow);
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

                PackageRepo packageRepo = new PackageRepo(uow);
                List<CardInfoUser> userCards = packageRepo.getCardsByUid(user);
                for (CardInfoUser card : userCards) {
                    if(card.getCid().equals(tradingDeal.getCardToTrade()) && packageRepo.checkCardIsEnabledToTrade(tradingDeal.getCardToTrade())) {
                        TradingRepo tradingRepo = new TradingRepo(uow);
                        packageRepo.lockCard(tradingDeal.getCardToTrade());
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
}
