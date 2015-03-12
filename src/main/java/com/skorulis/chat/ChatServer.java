/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skorulis.chat;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author alex
*/

public class ChatServer extends WebSocketServer {
    
    public ChatServer( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
    }
    
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }
    
    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println( conn + " has left the room!" );
    }
    
    @Override
    public void onMessage( WebSocket conn, String message ) {
        System.out.println( conn + ": " + message );
        conn.send(message);
    }
    
    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
                // some errors like port binding failed may not be assignable to a specific websocket
        }
    }
    
    
}

