package at.fhtw.mtcgapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class BattleService implements Service {

    private final BattleController battleController;

    public BattleService(BattleController battleController) {
        this.battleController = battleController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.POST) {
            return this.battleController.battle(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
