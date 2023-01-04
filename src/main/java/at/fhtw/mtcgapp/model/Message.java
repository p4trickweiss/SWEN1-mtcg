package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Message {
    @JsonAlias({"Mid"})
    private int mid;
    @JsonAlias({"sender"})
    private String sender;
    @JsonAlias({"Recipient"})
    private String recipient;
    @JsonAlias({"Message"})
    private String message;

    public Message() {
    }

    public Message(int mid, String sender, String recipient, String message) {
        this.mid = mid;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
