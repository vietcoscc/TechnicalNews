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

    public Topic() {
    }

    public Topic(String content, String date, String time, int numberCare, int numberView, int numberReply, String mail) {
        this.content = content;
        this.date = date;
        this.time = time;
        this.numberCare = numberCare;
        this.numberView = numberView;
        this.numberReply = numberReply;
        this.mail = mail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getNumberCare() {
        return numberCare;
    }

    public void setNumberCare(int numberCare) {
        this.numberCare = numberCare;
    }

    public int getNumberView() {
        return numberView;
    }

    public void setNumberView(int numberView) {
        this.numberView = numberView;
    }

    public int getNumberReply() {
        return numberReply;
    }

    public void setNumberReply(int numberReply) {
        this.numberReply = numberReply;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
