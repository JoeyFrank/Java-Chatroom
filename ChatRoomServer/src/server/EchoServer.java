package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
                //System.out.println("Client Connected.");

                //here we make a new thread for client
                ClientThread client = new ClientThread(s);
                client.start();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}


	public static void loadUsers() {
	    try {
	        BufferedReader in = new BufferedReader(new FileReader("users.txt"));
	        String username;
	        String password;

            for(int i = 0; i < 4; i++){
                username = in.readLine();
                password = in.readLine();
                validUsers.add(new User(username, password));
                //System.out.println("New user added: " + username + " " + password);
            }

        } catch (Exception e){
	        System.out.println(e);
        }
	}


	public static void serverMessage(String message){
	    System.out.println(message);
    }


    public static void sendToAll(ClientThread sender, String message){
        for(ClientThread client : EchoServer.clients){
            if(client != sender){
                client.receiveMessage(message);
            }
        }
    }


    public static boolean sendToUser(String recipient, String message){
        for(ClientThread client : EchoServer.clients){
            if(client.username.equals(recipient)){
                client.receiveMessage(message);
                return true;
            }
        }

        return false;
    }


    public static boolean clientConnected(){
	    if(numConnectedClients < MAX_CLIENTS) {
            numConnectedClients++;
            return true;
        } else {
	        return false;
        }
    }


    public static void clientDisconnected(){
	    numConnectedClients--;
    }
}


