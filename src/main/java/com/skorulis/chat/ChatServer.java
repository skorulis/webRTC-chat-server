/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skorulis.chat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 *
 * @author alex
*/

public class ChatServer extends WebSocketServer {
    
    private final Map<WebSocket,ChatConnection> connections;
    private final List<ChatConnection> offers;
    private final Gson gson;
    
    public ChatServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        connections = new HashMap<>();
        offers = new LinkedList<>();
        gson = new Gson();
    }
    
    private ChatConnection findOffer() {
        if(offers.size() > 0) {
            return offers.remove(0);
        }
        return null;
    }
    
    private void handleChatRequest(ChatConnection conn) {
        ChatConnection other = findOffer();
        ChatControlMessage message = new ChatControlMessage();
        if(other != null) {
            System.out.println("Found available connection");
            message.type = ChatControlMessage.CCT_CHAT_OFFER;
            message.payload = gson.toJson(other.sdp);
            other.chattingWith = conn;
            conn.chattingWith = other;
            for(ICECandidateModel ice : other.waitingCandidates) {
                ChatControlMessage iceMessage = new ChatControlMessage();
                iceMessage.type = ChatControlMessage.CCT_ICE_CANDIDATE;
                iceMessage.payload = gson.toJson(ice);
                sendMessage(iceMessage, conn);
            }
        } else {
            message.type = ChatControlMessage.CCT_CHAT_INIT;
            
        }
        sendMessage(message, conn);
    }
    
    private void handleChatAnswer(ChatConnection conn, SDPModel answer) {
        conn.sdp = answer;
        ChatControlMessage message = new ChatControlMessage();
        message.type = ChatControlMessage.CCT_CHAT_ANSWER;
        message.payload = gson.toJson(answer);
        sendMessage(message, conn.chattingWith);
    }
    
    private void handleChatOffer(ChatConnection conn, SDPModel offer) {
        conn.sdp = offer;
        offers.add(conn);
    }
    
    private void handleIceCandidate(ChatConnection conn, ICECandidateModel ice)  {
        if(conn.chattingWith != null) {
            ChatControlMessage message = new ChatControlMessage();
            message.type = ChatControlMessage.CCT_ICE_CANDIDATE;
            message.payload = gson.toJson(ice);
            sendMessage(message, conn.chattingWith);
        } else {
            conn.waitingCandidates.add(ice);
        }
    }
    
    private void handleDisconnect(ChatConnection conn) {
        if(conn.chattingWith != null) {
            sendMessage(new ChatControlMessage(ChatControlMessage.CCT_CHAT_DISCONNECT), conn.chattingWith);
            conn.chattingWith.clear();
        }
        conn.clear();
    }
    
    private void sendMessage(ChatControlMessage message, ChatConnection conn) {
        String json = gson.toJson(message);
        conn.socket.send(json);
    }
    
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        connections.put(conn, new ChatConnection(conn));
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }
    
    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println( conn + " has left the room!" );
        ChatConnection chatConn = connections.get(conn);
        connections.remove(conn);
        offers.remove(chatConn);
    }
    
    @Override
    public void onMessage( WebSocket conn, String message ) {
        //Using binary messages
    }
    
    @Override
    public void onMessage(WebSocket ws, ByteBuffer bb) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bb.array());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            ChatControlMessage control = gson.fromJson(reader, ChatControlMessage.class);
            ChatConnection conn = connections.get(ws);
            if(control.isChatRequest()) {
                handleChatRequest(conn);
            } else if(control.isChatOffer()) {
                SDPModel sdp = gson.fromJson(control.payload, SDPModel.class);
                handleChatOffer(conn, sdp);
            } else if(control.isChatAnswer()) {
                SDPModel sdp = gson.fromJson(control.payload, SDPModel.class);
                handleChatAnswer(conn, sdp);
            } else if(control.isIceCandidate()) {
                ICECandidateModel ice = gson.fromJson(control.payload, ICECandidateModel.class);
                handleIceCandidate(conn,ice);
            } else if(control.isDisconnect()) {
                handleDisconnect(conn);
            }
            System.out.println("Got control " + control.type);
        } catch(JsonSyntaxException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
                // some errors like port binding failed may not be assignable to a specific websocket
        }
    }
    
    
}

