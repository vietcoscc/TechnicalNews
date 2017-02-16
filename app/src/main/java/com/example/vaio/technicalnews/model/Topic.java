package com.example.vaio.technicalnews.model;

import java.util.ArrayList;

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

    private ArrayList<String> arrComment;

    public Topic() {
    }

    public Topic(String content, String date, String time, int numberCare, int numberView, int numberReply, String mail, String name, ArrayList<String> arrComment) {
        this.content = content;
        this.date = date;
        this.time = time;
        this.numberCare = numberCare;
        this.numberView = numberView;
        this.numberReply = numberReply;
        this.mail = mail;
        this.name = name;
        this.arrComment = arrComment;
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

    public ArrayList<String> getArrComment() {
        return arrComment;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNumberCare(int numberCare) {
        this.numberCare = numberCare;
    }

    public void setNumberView(int numberView) {
        this.numberView = numberView;
    }

    public void setNumberReply(int numberReply) {
        this.numberReply = numberReply;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArrComment(ArrayList<String> arrComment) {
        this.arrComment = arrComment;
    }
}
