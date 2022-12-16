package at.fhtw.mtcgapp.service.tradings;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TradingsService implements Service {

    private final TradingsController tradingsController;

    public TradingsService(TradingsController tradingsController) {
        this.tradingsController = tradingsController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET) {
            return this.tradingsController.getTradingDeals(request);
        }
        if(request.getMethod() == Method.POST) {
            return this.tradingsController.createTradingDeal(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
