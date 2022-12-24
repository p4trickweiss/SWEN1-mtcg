package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class BattleLogEntry {
    @JsonAlias({"Bid"})
    private int bid;
    @JsonAlias({"PlayerA"})
    private String playerA;
    @JsonAlias({"PlayerB"})
    private String playerB;
    @JsonAlias({"CardPlayerA"})
    private String cardPlayerA;
    @JsonAlias({"CardPlayerB"})
    private String cardPlayerB;
    @JsonAlias({"DamageCardA"})
    private int damageCardA;
    @JsonAlias({"DamageCardB"})
    private int damageCardB;
    @JsonAlias({"Finished"})
    private boolean finished;

    public BattleLogEntry() {
    }

    public BattleLogEntry(int bid, String playerA, String playerB, String cardPlayerA, String cardPlayerB, int damageCardA, int damageCardB, boolean finished) {
        this.bid = bid;
        this.playerA = playerA;
        this.playerB = playerB;
        this.cardPlayerA = cardPlayerA;
        this.cardPlayerB = cardPlayerB;
        this.damageCardA = damageCardA;
        this.damageCardB = damageCardB;
        this.finished = finished;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getPlayerA() {
        return playerA;
    }

    public void setPlayerA(String playerA) {
        this.playerA = playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public void setPlayerB(String playerB) {
        this.playerB = playerB;
    }

    public String getCardPlayerA() {
        return cardPlayerA;
    }

    public void setCardPlayerA(String cardPlayerA) {
        this.cardPlayerA = cardPlayerA;
    }

    public String getCardPlayerB() {
        return cardPlayerB;
    }

    public void setCardPlayerB(String cardPlayerB) {
        this.cardPlayerB = cardPlayerB;
    }

    public int getDamageCardA() {
        return damageCardA;
    }

    public void setDamageCardA(int damageCardA) {
        this.damageCardA = damageCardA;
    }

    public int getDamageCardB() {
        return damageCardB;
    }

    public void setDamageCardB(int damageCardB) {
        this.damageCardB = damageCardB;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
