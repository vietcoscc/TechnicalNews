package com.example.vaio.technicalnews.model.forum;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 12/28/2016.
 */

public class Topic implements Serializable {
    private String key;
    private String groupName;
    private String childName;

    private String uid; // user ID

    private String subject;
    private String content;
    private String date;
    private String time;

    private int numberCare;
    private int numberView;
    private int numberReply;

    private UserInfo userInfo;

    private ArrayList<Comment> arrComment;

    public Topic() {

    }

    public Topic(String uid, String subject, String content, String date, String time, int numberCare, int numberView, int numberReply, ArrayList<Comment> arrComment) {
        this.uid = uid;
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.time = time;
        this.numberCare = numberCare;
        this.numberView = numberView;
        this.numberReply = numberReply;
        this.arrComment = arrComment;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public ArrayList<Comment> getArrComment() {
        return arrComment;
    }

    public void setArrComment(ArrayList<Comment> arrComment) {
        this.arrComment = arrComment;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
