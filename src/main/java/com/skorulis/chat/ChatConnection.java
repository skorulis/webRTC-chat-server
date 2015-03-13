/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skorulis.chat;

import org.java_websocket.WebSocket;

/**
 *
 * @author alex
 */
public class ChatConnection {
    
    public final WebSocket socket;
    public SDPModel sdp;
    public ChatConnection chattingWith;
    
    public ChatConnection(WebSocket socket) {
        this.socket = socket;
    }
    
    
}
