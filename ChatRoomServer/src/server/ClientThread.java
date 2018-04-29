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
                //System.out.println("> Client: " + line);
                //outs.println("Message Sent -  " + line);
            }

            //socket.close();
            //System.out.println("Client Closed.");

        } catch (Exception e) {
            //this will happen on disconnection, so you can do user cleanup here
            System.out.println(this.username + "left");
            EchoServer.clients.remove(this);
        }
    }

    private void parseCommand(String input) {
        String[] tokens = input.split(" ");

        if (!tokens[0].equals("login") && !isLoggedIn) {
            outs.println("Denied. Please login first.");
        }

        switch (tokens[0]) {
            case "login":
                login(tokens);
                break;

            case "send":
                sendMessage();
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
                    System.out.println(user.username + " login");
                    outs.println("Login Confirmed");
                    this.isLoggedIn = true;
                    return;
                }
            }
        }

        outs.println("Invalid Username or Password");
    }

    private void sendMessage() {
        System.out.println("send message command");
    }

    private void listUsers() {
        String usernames = EchoServer.clients.get(0).username;

        for(int i = 1; i < EchoServer.clients.size(); i++){
            usernames += (", " + EchoServer.clients.get(i).username);
        }

        outs.println(usernames);
    }

    private void logout() {
        System.out.println("logout command");
    }

    private void invalidInput() {
        System.out.println("invalid command");
    }

    public void recieveMessage() {
        System.out.println("login command");
    }

    public void setUsername(String username){
        this.username = username;
    }
}