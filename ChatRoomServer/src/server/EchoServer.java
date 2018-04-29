package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class EchoServer
{
    public static final int MAXCLIENTS = 3;

    public static ArrayList<ClientThread> clients = new ArrayList<>(); //clients in system
    public static ArrayList<User> validUsers = new ArrayList<>(); //valid client names

	public static void main(String args[])
	{
        loadUsers();

		try
		{
			ServerSocket echoServer = new ServerSocket(16471);
			//Try not to use port number < 2000. 
			System.out.println("Waiting for a client to connect..."); 
			while (true)
			{
				Socket s = echoServer.accept(); //blocks until client connects
                System.out.println("Client Connected.");

                //here we make a new thread for client
                ClientThread client = new ClientThread(s);
                clients.add(client); //add client list so we can keep track of all clients
                client.start();
			}
		}
		catch (IOException e)
		{
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
}


