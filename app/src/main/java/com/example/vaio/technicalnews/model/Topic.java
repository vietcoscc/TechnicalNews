package com.example.vaio.technicalnews.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 12/28/2016.
 */

public class Topic implements Serializable {
    private String key;
    private String groupName;
    private String childName;

    private String subject;
    private String content;
    private String date;
    private String time;

    private int numberCare;
    private int numberView;
    private int numberReply;

    private String mail;
    private String name;

    private ArrayList<Comment> arrComment;
    private String photoPath;

    public Topic() {
    }

    public Topic(String key, String groupName, String childName, String subject, String content, String date, String time, int numberCare, int numberView, int numberReply, String mail, String name, ArrayList<Comment> arrComment, String photoPath) {
        this.key = key;
        this.groupName = groupName;
        this.childName = childName;
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.time = time;
        this.numberCare = numberCare;
        this.numberView = numberView;
        this.numberReply = numberReply;
        this.mail = mail;
        this.name = name;
        this.arrComment = arrComment;
        this.photoPath = photoPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Comment> getArrComment() {
        return arrComment;
    }

    public void setArrComment(ArrayList<Comment> arrComment) {
        this.arrComment = arrComment;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
