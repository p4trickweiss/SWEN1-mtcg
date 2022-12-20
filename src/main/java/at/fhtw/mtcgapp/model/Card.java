package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Card {
    @JsonAlias({"Id"})
    private String cid;
    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Damage"})
    private int damage;
    @JsonAlias({"Type"})
    private String type;
    @JsonAlias({"Element"})
    private String element;
    @JsonAlias({"inDeck"})
    private boolean in_deck;
    @JsonAlias({"isLocked"})
    private boolean is_locked;
    @JsonAlias({"fkPid"})
    private int fk_pid;
    @JsonAlias({"fkUid"})
    private int fk_uid;

    public Card() {}

    public Card(String cid, String name, int damage) {
        this.cid = cid;
        this.name = name;
        this.damage = damage;
    }

    public Card(String cid, String name, int damage, String type, String element, boolean in_deck, boolean is_locked, int fk_pid, int fk_uid) {
        this.cid = cid;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.element = element;
        this.in_deck = in_deck;
        this.is_locked = is_locked;
        this.fk_pid = fk_pid;
        this.fk_uid = fk_uid;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public boolean isIn_deck() {
        return in_deck;
    }

    public void setIn_deck(boolean in_deck) {
        this.in_deck = in_deck;
    }

    public boolean isIs_locked() {
        return is_locked;
    }

    public void setIs_locked(boolean is_locked) {
        this.is_locked = is_locked;
    }

    public int getFk_pid() {
        return fk_pid;
    }

    public void setFk_pid(int fk_pid) {
        this.fk_pid = fk_pid;
    }

    public int getFk_uid() {
        return fk_uid;
    }

    public void setFk_uid(int fk_uid) {
        this.fk_uid = fk_uid;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cid='" + cid + '\'' +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", type='" + type + '\'' +
                ", element='" + element + '\'' +
                ", in_deck=" + in_deck +
                ", is_locked=" + is_locked +
                ", fk_pid=" + fk_pid +
                ", fk_uid=" + fk_uid +
                '}';
    }
}
