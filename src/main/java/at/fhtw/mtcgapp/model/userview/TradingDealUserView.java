package at.fhtw.mtcgapp.model.userview;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TradingDealUserView {

    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"CardToTrade"})
    private String cardToTrade;
    @JsonAlias({"Type"})
    private String type;
    @JsonAlias({"MinimumDamage"})
    private int minimumDamage;

    public TradingDealUserView(String id, String cardToTrade, String type, int minimumDamage) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardToTrade() {
        return cardToTrade;
    }

    public void setCardToTrade(String cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(int minimumDamage) {
        this.minimumDamage = minimumDamage;
    }

    @Override
    public String toString() {
        return "TradingDealUserView{" +
                "id='" + id + '\'' +
                ", cardToTrade='" + cardToTrade + '\'' +
                ", type='" + type + '\'' +
                ", minimumDamage=" + minimumDamage +
                '}';
    }
}
