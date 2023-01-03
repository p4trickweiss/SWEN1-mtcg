package at.fhtw.mtcgapp.service.gift;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class GiftService implements Service {

    private final GiftController giftController;

    public GiftService(GiftController giftController) {
        this.giftController = giftController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST && request.getPathParts().get(1).equals("vc")) {
            return this.giftController.sendVc(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
