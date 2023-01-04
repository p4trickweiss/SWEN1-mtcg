package at.fhtw.mtcgapp.model.userview;

import com.fasterxml.jackson.annotation.JsonAlias;

public class MessageUserView {

    @JsonAlias({"Sender"})
    private String sender;
    @JsonAlias({"Message"})
    private String message;

    public MessageUserView() {
    }

    public MessageUserView(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
