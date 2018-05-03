package client;

import java.io.*;
import java.net.*;

/*
Joseph Frank
May 2, 2018

Description:
    This is the client component for a client-server chat room project for Computer Networks 1. This client sends
    user messages up to the server, and receives all incoming messages from the server. All command checking and
    authentication done on server. This application starts a new thread to listen for incoming messages, while also
    waiting for user input.

 */

public class EchoClient
{
	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("EchoClient MachineName");
		}
	
		InputStreamReader convert = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(convert);
		
		try
		{
			Socket echoClient = new Socket(args[0], 16471);
			PrintStream outs = new PrintStream(echoClient.getOutputStream()); //output to the server
			BufferedReader ins = new BufferedReader(new InputStreamReader(echoClient.getInputStream())); //messages from server

            //launch a thread to listen for any incoming message from server
            Thread serverMessageListener = new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = ins.readLine();

                        //if message is null the server is no longer available, so exit
                        if(serverMessage == null){
                            echoClient.close();
                            System.exit(0);
                            return;
                        }

                        System.out.println("> " + serverMessage); //server messages
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            serverMessageListener.start();

            //user input loop
            while(true) {
                String line = stdin.readLine(); //read input on client end
                outs.println(line); //send input to server
                if(echoClient.isClosed()){
                    return;
                }
            }
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}


