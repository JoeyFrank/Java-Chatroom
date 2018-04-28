package server;

import java.io.*;
import java.net.*;

public class EchoServer
{
	public static void main(String args[])
	{
		try
		{
			ServerSocket echoServer = new ServerSocket(16471);
			//Try not to use port number < 2000. 
			System.out.println("Waiting for a client to connect..."); 
			while (true)
			{
				Socket s = echoServer.accept(); //blocks until client connects
                System.out.println("Client Connected.");
//                ClientThread client = new ClientThread(s);
//                client.start();

				//here we make a new thread for client
				BufferedReader ins = new BufferedReader(new InputStreamReader(s.getInputStream())); //message from client
				PrintStream outs = new PrintStream(s.getOutputStream()); //output to client
				String line = ins.readLine(); //reads input from the client
				outs.println("Message Sent -  " + line);
				s.close();
				System.out.println("Client Closed.");
			}
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}


