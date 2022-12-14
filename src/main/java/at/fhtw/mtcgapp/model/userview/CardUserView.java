package at.fhtw.mtcgapp.model.userview;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CardUserView {
    @JsonAlias({"Id"})
    private String cid;
    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Damage"})
    private int damage;

    public CardUserView() {
    }

    public CardUserView(String cid, String name, int damage) {
        this.cid = cid;
        this.name = name;
        this.damage = damage;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
