package com.example.vaio.technicalnews.model;

/**
 * Created by vaio on 31/03/2017.
 */

public class MyNotification {
    String key;
    int position;
    String fromName;
    String topicName;
    String contentComment;
    String from;
    String to;

    public MyNotification() {
    }

    public MyNotification(String key, int position, String fromName, String topicName, String contentComment, String from, String to) {
        this.key = key;
        this.position = position;
        this.topicName = topicName;
        this.fromName = fromName;
        this.contentComment = contentComment;
        this.from = from;
        this.to = to;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getContentComment() {
        return contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
