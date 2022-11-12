package at.fhtw.mtcgapp.service.session;

import at.fhtw.mtcgapp.dal.Singleton;
import at.fhtw.mtcgapp.dal.repos.UserRepo;
import at.fhtw.mtcgapp.model.User;

import java.sql.Connection;

public class SessionsUOW {

    private Connection connection;
    private UserRepo userRepo;

    public SessionsUOW() {
        this.connection = Singleton.INSTANCE.getConnection();
        this.userRepo = new UserRepo(this.connection);
    }

    public void logIn(User user) throws Exception {
        User userData = userRepo.getUser(user.getUsername());
        if((user.getUsername().equals(userData.getUsername())) && (user.getPassword().equals(userData.getPassword()))) {
            user.setToken(user.getUsername() + "-mtcgToken");
            this.userRepo.createSession(user);
        } else {
            throw new Exception("Invalid username/password provided");
        }
    }
}
