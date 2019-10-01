package com.example.funnychat.chat;

public class ChatMessage {

    public boolean left;
    public String name;
    public String message;

    public ChatMessage(boolean left, String name, String message) {
        super();
        this.left = left;
        this.name = name;
        this.message = message;
    }
}
