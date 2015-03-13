/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skorulis.chat;

import java.util.ArrayList;
import java.util.List;
import org.java_websocket.WebSocket;

/**
 *
 * @author alex
 */
public class ChatConnection {
    
    public final WebSocket socket;
    public final List<ICECandidateModel> waitingCandidates;
    public SDPModel sdp;
    public ChatConnection chattingWith;
    
    public ChatConnection(WebSocket socket) {
        this.socket = socket;
        waitingCandidates = new ArrayList<>();
    }
    
    public void clear() {
        waitingCandidates.clear();
        sdp = null;
        chattingWith = null;
    }
    
}
