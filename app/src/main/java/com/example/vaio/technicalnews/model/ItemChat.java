package com.example.vaio.technicalnews.model;

import java.io.Serializable;

/**
 * Created by vaio on 16/03/2017.
 */

public class ItemChat implements Serializable {
    private String name;
    private String chat;
    private String date;
    private String time;

    public ItemChat() {
    }

    public ItemChat(String name, String chat, String date, String time) {
        this.name = name;
        this.chat = chat;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
