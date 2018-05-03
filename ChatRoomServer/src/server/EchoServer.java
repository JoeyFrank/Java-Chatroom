package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/*
Joseph Frank
May 2, 2018

Description:
    This is the server component for a client-server chat room project for Computer Networks 1. This server handles
    all incoming clients by starting a new thread per client. The client has the ability to login, send messages to all
    or one user, list users in chat room, and logout.

 */

public class EchoServer {

    private static final int MAX_CLIENTS = 3;
    private static int numConnectedClients = 0;

    public static ArrayList<ClientThread> clients = new ArrayList<>(); //clients in system
    public static ArrayList<User> validUsers = new ArrayList<>(); //valid client names

	public static void main(String args[])
	{
        loadUsers(); //load up users to verify login credentials

		try {
			ServerSocket echoServer = new ServerSocket(16471);
			System.out.println("My chat room server. Version Two.");

			//client connection loop
			while(true) {
				Socket s = echoServer.accept(); //blocks until client connects

                //here we make a new thread for client
                ClientThread client = new ClientThread(s);
                client.start();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}


	//helper function to load user authentication data into sever, all data stored in users.txt
	public static void loadUsers() {
	    try {
	        BufferedReader in = new BufferedReader(new FileReader("users.txt"));
	        String username;
	        String password;

            //get all users in file, add to validUsers array
            for(int i = 0; i < 4; i++){
                username = in.readLine();
                password = in.readLine();
                validUsers.add(new User(username, password));
            }

        } catch (Exception e){
	        System.out.println(e);
        }
	}

    //output to server log
	public static void serverMessage(String message){
	    System.out.println(message);
    }

    //send a message to all users
    public static void sendToAll(ClientThread sender, String message){
        for(ClientThread client : EchoServer.clients){
            if(client != sender){
                client.receiveMessage(message);
            }
        }
    }

    //send a message to a specific user
    public static boolean sendToUser(String recipient, String message){
        for(ClientThread client : EchoServer.clients){
            if(client.username.equals(recipient)){
                client.receiveMessage(message);
                return true;
            }
        }

        return false;
    }

    //see if you can add client without hitting max clients
    public static boolean clientConnected(){
	    if(numConnectedClients < MAX_CLIENTS) {
            numConnectedClients++;
            return true;
        } else {
	        return false;
        }
    }

    //decrement connected client count
    public static void clientDisconnected(){
	    numConnectedClients--;
    }
}


