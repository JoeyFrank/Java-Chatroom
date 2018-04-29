package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class EchoServer {

    public static final int MAXCLIENTS = 3;

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
                System.out.println("Client Connected.");

                //here we make a new thread for client
                ClientThread client = new ClientThread(s);
                clients.add(client); //add client list so we can keep track of all clients
                client.start();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}


	public static void loadUsers() {
	    try{
            Object obj = new JSONParser().parse(new FileReader("users.json"));
            JSONArray userJSON = (JSONArray)obj;

            for(int i = 0; i < 4; i++){
                JSONObject userObj = (JSONObject) userJSON.get(i);
                validUsers.add(new User((String)userObj.get("username"), (String)userObj.get("password")));
            }

        } catch (Exception e){
	        System.out.println(e);
        }
	}

	public static void serverMessage(String message){
	    System.out.println("> " + message);
    }

    public static void sendToAll(ClientThread sender, String message){
        for(ClientThread client : EchoServer.clients){
            if(client != sender){
                client.recieveMessage(message);
            }
        }
    }

    public static boolean sendToUser(String recipient, String message){
        for(ClientThread client : EchoServer.clients){
            if(client.username.equals(recipient)){
                client.recieveMessage(message);
                return true;
            }
        }

        return false;
    }
}


