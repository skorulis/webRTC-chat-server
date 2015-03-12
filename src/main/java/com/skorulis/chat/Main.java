package com.skorulis.chat;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port = 8123;
        ChatServer server;
        try {
            server = new ChatServer(port);
            server.start();
            System.out.println("Started web socket on port " + port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
