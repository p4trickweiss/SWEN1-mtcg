package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TradingDeal {
    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"CardToTrade"})
    private String cardToTrade;
    @JsonAlias({"Type"})
    private String type;
    @JsonAlias({"MinimumDamage"})
    private int minimumDamage;
    @JsonAlias({"fkUid"})
    private int fkUid;

    public TradingDeal() {
    }

    public TradingDeal(String id, String cardToTrade, String type, int minimumDamage) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
    }

    public TradingDeal(String id, String cardToTrade, String type, int minimumDamage, int fkUid) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.type = type;
        this.minimumDamage = minimumDamage;
        this.fkUid = fkUid;
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

    public int getFkUid() {
        return fkUid;
    }

    public void setFkUid(int fkUid) {
        this.fkUid = fkUid;
    }

    @Override
    public String toString() {
        return "TradingDeal{" +
                "id='" + id + '\'' +
                ", cardToTrade='" + cardToTrade + '\'' +
                ", type='" + type + '\'' +
                ", minimumDamage=" + minimumDamage +
                '}';
    }
}
