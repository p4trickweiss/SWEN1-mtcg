package at.fhtw.mtcgapp.service.transactions;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TransactionsService implements Service {
    private final TransactionsController transactionsController;

    public TransactionsService(TransactionsController transactionsController) {
        this.transactionsController = transactionsController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST && request.getPathParts().get(1).equals("packages")) {
            return this.transactionsController.acquirePackage(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
