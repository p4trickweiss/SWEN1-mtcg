package at.fhtw.mtcgapp.model.userview;

import com.fasterxml.jackson.annotation.JsonAlias;

public class UserDataUserView {

    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Bio"})
    private String bio;
    @JsonAlias({"Image"})
    private String image;

    public UserDataUserView(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "UserDataUserView{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
