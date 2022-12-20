package at.fhtw.mtcgapp.service.transactions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.CardRepo;
import at.fhtw.mtcgapp.dal.repos.PackageRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.userview.CardUserView;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JacksonException;

import java.util.List;

public class TransactionsController extends Controller {
    public TransactionsController() {
    }

    public Response acquirePackage(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();

        try {
            UserRepo userRepo = new UserRepo(uow);
            PackageRepo packageRepo = new PackageRepo(uow);
            CardRepo cardRepo = new CardRepo(uow);

            User user = userRepo.getUserByToken(token);
            if (user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            if (user.getCoins() < 5) {
                uow.commitTransaction();
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Not enough money for buying a card package\" }"
                );
            }

            Package cardPackage = packageRepo.getPackage();
            if(cardPackage == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"No card package available for buying\" }"
                );
            }

            packageRepo.makePackageUnavailable(cardPackage);
            userRepo.payPackage(cardPackage, user);
            cardRepo.acquireCardsByPid(cardPackage, user);

            List<CardUserView> cards = cardRepo.getCardsByPid(cardPackage);
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
}
