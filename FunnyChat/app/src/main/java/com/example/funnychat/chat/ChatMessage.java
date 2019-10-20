package com.example.funnychat.chat;

import android.graphics.Bitmap;

public class ChatMessage {

    public String position;
    public String name;
    public String type;
    public String message;

    public ChatMessage(String position, String name, String type, String message) {
        super();
        this.position = position;
        this.name = name;
        this.type = type;
        this.message = message;
    }
}
