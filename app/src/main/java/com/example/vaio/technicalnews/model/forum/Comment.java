package com.example.vaio.technicalnews.model.forum;

import java.io.Serializable;

/**
 * Created by vaio on 23/03/2017.
 */

public class Comment implements Serializable {
    private String key;

    private String uid;

    private String comment;
    private String date;
    private String time;

    public Comment() {
    }

    public Comment(String uid, String comment, String date, String time) {

        this.uid = uid;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
