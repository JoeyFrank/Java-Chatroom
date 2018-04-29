package server;

import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientThread extends Thread {

    Socket socket;
    boolean isLoggedIn = false;
    String username = null;

    BufferedReader ins; //from client
    PrintStream outs; //to client

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
            }

        } catch (Exception e) {
            //this will happen on disconnection, so you can do user cleanup here
            EchoServer.serverMessage(this.username + " left");
            EchoServer.clients.remove(this);
        }
    }

    private void parseCommand(String input) {
        String[] tokens = input.split(" ");

        if (!tokens[0].equalsIgnoreCase("login") && !isLoggedIn) {
            outs.println("Denied. Please login first.");
        }

        switch (tokens[0].toLowerCase()) {
            case "login":
                login(tokens);
                break;

            case "send":
                sendMessage(tokens);
                break;

            case "who":
                listUsers();
                break;

            case "logout":
                logout();
                break;

            default:
                invalidInput();
        }
    }

    private void login(String[] input) {
        //System.out.println("login command");

        if (input.length < 3) {
            outs.println("Invalid Login.");
            return;
        }

        setUsername(input[1]);

        for(User user : EchoServer.validUsers){
            if (user.username.equals(this.username)){
                if(user.password.equals(input[2])){
                    outs.println("Login Confirmed");
                    setUsername(user.username);
                    this.isLoggedIn = true;
                    EchoServer.serverMessage(this.username + " login");
                    EchoServer.sendToAll(this, this.username + " joined");
                    return;
                }
            }
        }

        outs.println("Invalid Username or Password");
    }

    private void sendMessage(String[] input) {
        EchoServer.serverMessage("send message command");

        if(input.length < 3){
            outs.println("Usage: send [username/all] [message]");
        }

        String message = this.username + ":";
        for(int i = 2; i < input.length; i++){
            message += " " + input[i];
        }

        if(input[1].equalsIgnoreCase("all")){
            EchoServer.sendToAll(this, message);
        } else {
            boolean success = EchoServer.sendToUser(input[1], message);
            if(success){
                return;
            }

            outs.println("Could not find user.");
        }
    }

    private void listUsers() {
        String usernames = EchoServer.clients.get(0).username;

        for(int i = 1; i < EchoServer.clients.size(); i++){
            usernames += (", " + EchoServer.clients.get(i).username);
        }

        outs.println(usernames);
    }

    private void logout() {
        EchoServer.serverMessage("logout command");
    }

    private void invalidInput() {
        EchoServer.serverMessage("Invalid command");
    }

    public void recieveMessage(String message) {
        outs.println(message);
    }

    public void setUsername(String username){
        this.username = username;
    }
}