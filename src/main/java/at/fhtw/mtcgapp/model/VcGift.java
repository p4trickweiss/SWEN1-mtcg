package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class VcGift {
    @JsonAlias({"Recipient"})
    private String recipient;
    @JsonAlias({"Amount"})
    private int amount;

    public VcGift() {
    }

    public VcGift(String recipient, int amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
