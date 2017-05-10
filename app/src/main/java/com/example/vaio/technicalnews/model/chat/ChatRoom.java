package com.example.vaio.technicalnews.model.chat;

import java.io.Serializable;

/**
 * Created by vaio on 17/04/2017.
 */

public class ChatRoom implements Serializable {
    private String key;
    private String name;

    public ChatRoom() {
    }

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
