package com.example.vaio.technicalnews.model;

/**
 * Created by vaio on 12/28/2016.
 */

public class Topic {
    private String content;
    private String date;
    private String time;

    private int numberCare;
    private int numberView;
    private int numberReply;

    private String mail;
    private String name;

    public Topic() {
    }

    public Topic(String content, String date, String time, int numberCare, int numberView, int numberReply, String mail, String name) {
        this.content = content;
        this.date = date;
        this.time = time;
        this.numberCare = numberCare;
        this.numberView = numberView;
        this.numberReply = numberReply;
        this.mail = mail;
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumberCare() {
        return numberCare;
    }

    public int getNumberView() {
        return numberView;
    }

    public int getNumberReply() {
        return numberReply;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }
}
