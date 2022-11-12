package at.fhtw.mtcgapp.service.user;

import at.fhtw.mtcgapp.dal.Singleton;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;

import java.sql.Connection;

public class UserUOW {

    private Connection connection;
    private UserRepo userRepo;

    public UserUOW() {
        this.connection = Singleton.INSTANCE.getConnection();
        this.userRepo = new UserRepo(this.connection);
    }

    public void addUser(User user) throws Exception {
        if(this.userRepo.getUser(user.getUsername()) == null) {
            this.userRepo.createUser(user);
        } else {
            throw new Exception("User already exists");
        }
    }
}
