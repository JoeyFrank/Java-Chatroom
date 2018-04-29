package client;

import java.io.*;
import java.net.*;

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

            //listen for any incoming message from server
            Thread serverMessageListener = new Thread(){
                public void run(){
                    try {
                        while (true) {
                            System.out.println(ins.readLine()); //server confirmation
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            };
            serverMessageListener.start();

            //user input loop
            while(true) {
                System.out.print("> ");
                String line = stdin.readLine(); //read input on client end
                outs.println(line); //send input to server
                //System.out.println("Server says: " + ins.readLine());
            }
			
			//echoClient.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}


