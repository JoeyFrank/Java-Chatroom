package server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    public String username = null;

    private boolean isLoggedIn = false;
    private boolean logoutRequested = false;

    private Socket socket;
    private BufferedReader ins; //from client
    private PrintStream outs; //to client
    private User userObject;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            ins = new BufferedReader(new InputStreamReader(socket.getInputStream())); //message from client
            outs = new PrintStream(socket.getOutputStream()); //output to client
            outs.println("My chat room client. Version Two.");

            //listening for client messages
            while (true) {
                String line = ins.readLine(); //waits for input from the client
                parseCommand(line);
                if(logoutRequested || line == null){
                    socket.close();
                    throw new Exception(); //will trigger user logout
                }
            }

        } catch (Exception e) {
            //cleanup for all user disconnection handled the same through exceptions
            if(this.isLoggedIn) {
                EchoServer.serverMessage(this.username + " logout");
                EchoServer.sendToAll(this, this.username + " left");
                EchoServer.clients.remove(this);
                userObject.isLoggedIn = false;
                EchoServer.clientDisconnected();
            }
            return;
        }
    }

    private void parseCommand(String input) {
        if(input == null){
            invalidInput();
            return;
        }

        String[] tokens = input.split(" ");
        if(tokens == null || tokens.length == 0){
            invalidInput();
            return;
        }

        if (!tokens[0].equalsIgnoreCase("login") && !isLoggedIn) {
            outs.println("Denied. Please login first.");
            return;
        }

        switch (tokens[0].toLowerCase()) {
            case "login":
                login(tokens);
                break;

            case "send":
                sendMessage(tokens);
                break;

            case "who":
                listUsers(tokens);
                break;

            case "logout":
                logout(tokens);
                break;

            default:
                invalidInput();
        }
    }

    private void login(String[] input) {
        //stop user from logging in twice
        if(this.isLoggedIn){
            outs.println("You are already logged in");
            return;
        }

        //check for invalid usage
        if (input.length < 3) {
            outs.println("Invalid Login - Usage: login [username] [password]");
            return;
        }

        for(User user : EchoServer.validUsers){
            if (user.username.equals(input[1])){
                if(user.password.equals(input[2])){
                    if(!user.isLoggedIn) {
                        if (EchoServer.clientConnected()) {
                            outs.println("Login Confirmed");
                            setUsername(user.username);
                            this.isLoggedIn = true;
                            user.isLoggedIn = true;

                            userObject = user;

                            EchoServer.clients.add(this); //add client list so we can keep track of all clients
                            EchoServer.serverMessage(this.username + " login");
                            EchoServer.sendToAll(this, this.username + " joined");
                            return;
                        } else {
                            outs.println("Chat room full, disconnecting");
                            this.logoutRequested = true;
                            return;
                        }
                    } else {
                        outs.println("User account already logged in");
                        return;
                    }
                }
            }
        }

        outs.println("Invalid Username or Password");
    }

    private void sendMessage(String[] input) {

        if(input.length < 3){
            outs.println("Invalid send - Usage: send [username/all] [message]");
            return;
        }

        String message = "";
        String serverDialog = this.username + " (to " + input[1] + "):";
        for(int i = 2; i < input.length; i++){
            message += " " + input[i];
            serverDialog += " " + input[i];
        }

        if(input[1].equalsIgnoreCase("all")){
            message = this.username + ":" + message;
            EchoServer.sendToAll(this, message);
            EchoServer.serverMessage(message);
        } else {
            message = this.username + " (to you):" + message;
            boolean success = EchoServer.sendToUser(input[1], message);
            if(success){
                EchoServer.serverMessage(serverDialog);
                return;
            }

            outs.println("Could not find user.");
        }
    }

    private void listUsers(String[] input) {
        if(input.length > 1){
            outs.println("Invalid - 'who' takes no arguments");
            return;
        }

        String usernames = EchoServer.clients.get(0).username;

        for(int i = 1; i < EchoServer.clients.size(); i++){
            usernames += (", " + EchoServer.clients.get(i).username);
        }

        outs.println(usernames);
    }

    private void logout(String[] input) {
        if(input.length > 1){
            outs.println("Invalid - 'logout' takes no arguments");
            return;
        }

        outs.println("Disconnected");
        logoutRequested = true;
    }

    private void invalidInput() {
        outs.println("Invalid command");
    }

    public void receiveMessage(String message) {
        if(isLoggedIn) {
            outs.println(message);
        }
    }

    public void setUsername(String username){
        this.username = username;
    }
}