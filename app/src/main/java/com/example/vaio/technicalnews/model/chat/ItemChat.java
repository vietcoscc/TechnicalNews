package com.example.vaio.technicalnews.model.chat;

import java.io.Serializable;

/**
 * Created by vaio on 16/03/2017.
 */

public class ItemChat implements Serializable {
    private String key;

    private String uid;

    private String chat;
    private String date;
    private String time;

    public ItemChat() {
    }

    public ItemChat(String uid, String chat, String date, String time) {
        this.uid = uid;
        this.chat = chat;
        this.date = date;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
