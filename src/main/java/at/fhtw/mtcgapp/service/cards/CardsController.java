package at.fhtw.mtcgapp.service.cards;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.PackageRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.userview.CardUserView;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JacksonException;

import java.util.List;

public class CardsController extends Controller {

    public CardsController() {
    }

    public Response getCards(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();

        try {
            UserRepo userRepo = new UserRepo(uow);
            PackageRepo packageRepo = new PackageRepo(uow);

            User user = userRepo.getUserByToken(token);
            if(user == null) {
                uow.commitTransaction();
                return new Response(HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ \"message\" : \"Authentication information is missing or invalid\" }"
                );
            }

            List<CardUserView> cards = packageRepo.getCardsByUid(user);

            if(cards.isEmpty()) {
                uow.commitTransaction();
                return new Response(HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\" : \"The request was fine, but the user doesn't have any cards\" }"
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
}
