package server;

public class User {
    public String username;
    public String password;
    public boolean isLoggedIn;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
    }
}
