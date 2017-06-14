package com.example.vaio.technicalnews.model.application;

import com.example.vaio.technicalnews.model.forum.ChildForumItem;
import com.example.vaio.technicalnews.model.forum.GroupForumItem;
import com.example.vaio.technicalnews.model.forum.Topic;

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

    GroupForumItem group;
    ChildForumItem child;
    Topic topic;

    public MyNotification() {
    }

    public MyNotification(String fromName, String topicName, String contentComment, String from, String to, GroupForumItem group, ChildForumItem child, Topic topic) {
        this.fromName = fromName;
        this.topicName = topicName;
        this.contentComment = contentComment;
        this.from = from;
        this.to = to;
        this.group = group;
        this.child = child;
        this.topic = topic;
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

    public GroupForumItem getGroup() {
        return group;
    }

    public void setGroup(GroupForumItem group) {
        this.group = group;
    }

    public ChildForumItem getChild() {
        return child;
    }

    public void setChild(ChildForumItem child) {
        this.child = child;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
