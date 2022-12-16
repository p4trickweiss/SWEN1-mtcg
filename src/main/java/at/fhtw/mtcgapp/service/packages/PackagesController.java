package at.fhtw.mtcgapp.service.packages;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcgapp.controller.Controller;
import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.dal.repos.PackageRepo;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class PackagesController extends Controller {
    public PackagesController() {}

    public Response createPackage(Request request) {
        String token = request.getToken();
        UOW uow = new UOW();

        try {
            List<Card> cards = this.getObjectMapper().readValue(request.getBody(), new TypeReference<List<Card>>() {});
            try {
                PackageRepo packageRepo  = new PackageRepo(uow);
                UserRepo userRepo = new UserRepo(uow);
                User user = userRepo.getUserByToken(token);

                if(user == null) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\" : \"Authentication information is missing or invalid\" }"
                    );
                }

                if(!user.getUsername().equals("admin")) {
                    uow.commitTransaction();
                    return new Response(HttpStatus.FORBIDDEN,
                            ContentType.JSON,
                            "{ \"message\" : \"Provided user is not admin\" }"
                    );
                }

                int packageId = packageRepo.createPackageAndGetId();
                for (Card card : cards) {
                    card.setFk_pid(packageId);
                    packageRepo.createCard(card);
                }
                uow.commitTransaction();
                return new Response(HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \"message\" : \"Package and cards successfully created\" }"
                );
            } catch (DataAccessException dataAccessException) {
                uow.rollbackTransaction();
                if(dataAccessException.getMessage().equals("duplicate card")) {
                    return new Response(HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ \"message\" : \"At least one card in the packages already exists\" }"
                    );
                }
            }
            finally {
                uow.finishWork();
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
