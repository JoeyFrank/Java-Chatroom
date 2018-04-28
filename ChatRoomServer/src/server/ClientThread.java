package server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {

    Socket socket;
    String username;

    public ClientThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {

            BufferedReader ins = new BufferedReader(new InputStreamReader(socket.getInputStream())); //message from client
            PrintStream outs = new PrintStream(socket.getOutputStream()); //output to client
            String line = ins.readLine(); //reads input from the client
            outs.println("Message Sent -  " + line);
            socket.close();
            System.out.println("Client Closed.");

        } catch (Exception e) {
            System.out.println(e);
        }
    }


}
