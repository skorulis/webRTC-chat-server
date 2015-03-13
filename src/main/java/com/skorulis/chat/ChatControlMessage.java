/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.skorulis.chat;

/**
 *
 * @author alex
 */
public class ChatControlMessage {
    
    public static final String CCT_CHAT_REQUEST = "CCT_CHAT_REQUEST";
    public static final String CCT_CHAT_INIT = "CCT_CHAT_INIT";
    public static final String CCT_CHAT_OFFER = "CCT_CHAT_OFFER";
    public static final String CCT_CHAT_ANSWER = "CCT_CHAT_ANSWER";
    
    public String type;
    public Object payload;
    
    public boolean isChatRequest() {
        return type.equals(CCT_CHAT_REQUEST);
    }
    
}
