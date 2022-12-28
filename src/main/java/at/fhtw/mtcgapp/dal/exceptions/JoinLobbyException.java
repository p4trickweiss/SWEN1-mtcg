package at.fhtw.mtcgapp.dal.exceptions;

public class JoinLobbyException extends RuntimeException{

    public JoinLobbyException(String message) {
        super(message);
    }

    public JoinLobbyException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoinLobbyException(Throwable cause) {
        super(cause);
    }
}
