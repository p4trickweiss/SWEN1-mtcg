package at.fhtw.mtcgapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Package {
    @JsonAlias({"Id"})
    private int pid;
    @JsonAlias({"Price"})
    private int price;
    @JsonAlias({"Available"})
    private boolean is_available;

    public Package() {
    }

    public Package(int pid, int price, boolean is_available) {
        this.pid = pid;
        this.price = price;
        this.is_available = is_available;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isIs_available() {
        return is_available;
    }

    public void setIs_available(boolean is_available) {
        this.is_available = is_available;
    }
}
