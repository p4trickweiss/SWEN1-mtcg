package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TradingDeal {
    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"CardToTrade"})
    private String cardToTrade;
    @JsonAlias({"MinimumDamage"})
    private int minimumDamage;
    @JsonAlias({"Type"})
    private String type;
    @JsonAlias({"fkUid"})
    private int fkUid;

    public TradingDeal() {
    }

    public TradingDeal(String id, String cardToTrade, int minimumDamage, String type, int fkUid) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.minimumDamage = minimumDamage;
        this.type = type;
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
