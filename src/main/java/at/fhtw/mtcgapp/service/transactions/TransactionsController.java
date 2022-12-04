package at.fhtw.mtcgapp.service.transactions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.PackageRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.CardInfoUser;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JacksonException;

import java.sql.SQLException;
import java.util.List;

public class TransactionsController extends Controller {
    public TransactionsController() {
    }

    public Response acquirePackage(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();
        try {
            PackageRepo packageRepo = new PackageRepo(uow.getConnection());
            UserRepo userRepo = new UserRepo(uow.getConnection());
            uow.getConnection().setAutoCommit(false);
            User user = userRepo.getUserByToken(token);

            if (user == null) {
                uow.getConnection().commit();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            if (user.getCoins() < 5) {
                uow.getConnection().commit();
                return new Response(HttpStatus.FORBIDDEN,
                        ContentType.JSON,
                        "{ \"message\" : \"Not enough money for buying a card package\" }"
                );
            }

            Package cardPackage = packageRepo.getPackage();
            if(cardPackage == null) {
                uow.getConnection().commit();
                return new Response(HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"No card package available for buying\" }"
                );
            }

            packageRepo.makePackageUnavailable(cardPackage);
            userRepo.payPackage(cardPackage, user);
            packageRepo.acquireCardByPid(cardPackage, user);

            List<CardInfoUser> cards = packageRepo.getCardsByPid(cardPackage);
            String json = null;
            try {
                json = this.getObjectMapper().writeValueAsString(cards);
            } catch (JacksonException e) {
                e.printStackTrace();
            }
            uow.getConnection().commit();
            return new Response(HttpStatus.OK,
                    ContentType.JSON,
                    json
            );
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            if (uow.getConnection() != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    uow.getConnection().rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{\"message\" : \"Internal Server Error\" }"
        );
    }
}
