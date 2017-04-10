package com.example.vaio.technicalnews.model;

import java.io.Serializable;

/**
 * Created by vaio on 16/03/2017.
 */

public class ItemChat implements Serializable {
    private String key;
    private String name;
    private String email;
    private String chat;
    private String date;
    private String time;
    private String uid;
    private String uri;

    public ItemChat() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ItemChat(String name, String email, String chat, String date, String time, String uid, String uri) {
        this.name = name;
        this.email = email;
        this.chat = chat;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
