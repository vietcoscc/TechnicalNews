package com.example.vaio.technicalnews.model;

import java.io.Serializable;

/**
 * Created by vaio on 23/03/2017.
 */

public class Comment implements Serializable{
    private String photoPath;
    private String name;
    private String comment;
    private String date;
    private String time;

    public Comment() {
    }

    public Comment(String photoPath, String name, String comment, String date, String time) {
        this.photoPath = photoPath;
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
