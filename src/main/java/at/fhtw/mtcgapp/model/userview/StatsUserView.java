package at.fhtw.mtcgapp.model.userview;

import com.fasterxml.jackson.annotation.JsonAlias;

public class StatsUserView {

    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Elo"})
    private int Elo;
    @JsonAlias({"Wins"})
    private int wins;
    @JsonAlias({"Losses"})
    private int losses;

    public StatsUserView() {
    }

    public StatsUserView(String name, int elo, int wins, int losses) {
        this.name = name;
        Elo = elo;
        this.wins = wins;
        this.losses = losses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getElo() {
        return Elo;
    }

    public void setElo(int elo) {
        Elo = elo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
