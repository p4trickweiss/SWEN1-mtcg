package at.fhtw.mtcgapp.service.gift;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;
import at.fhtw.mtcgapp.model.VcGift;
import com.fasterxml.jackson.core.JsonProcessingException;

public class GiftController extends Controller {

    public Response sendVc(Request request) {
        UOW uow = new UOW();
        String token = request.getToken();

        try {
            VcGift vcGift = this.getObjectMapper().readValue(request.getBody(), VcGift.class);
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

                User to = userRepo.getUserByUsername(vcGift.getRecipient());
                if(to == null) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.NOT_FOUND,
                            ContentType.JSON,
                            "{ \"message\" : \"Recipient could not be found\" }"
                    );
                }

                if(user.getCoins() < vcGift.getAmount()) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"message\" : \"You do not own the desired amount of coins\" }"
                    );
                }

                userRepo.removeCoins(user, vcGift.getAmount());
                userRepo.addCoins(to, vcGift.getAmount());

                uow.commitTransaction();
                return new Response(HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\" : \"Transaction successful\" }"
                );
            }
            catch (DataAccessException dataAccessException) {
                uow.rollbackTransaction();
            }
            finally {
                uow.finishWork();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
