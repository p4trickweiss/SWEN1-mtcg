package at.fhtw.mtcgapp.service.messages;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class MessageService implements Service {

    private final MessageController messageController;

    public MessageService(MessageController messageController) {
        this.messageController = messageController;
    }

    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET) {
            return this.messageController.getMessages(request);
        }
        else if(request.getMethod() == Method.POST) {
            return this.messageController.sendMessage(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
