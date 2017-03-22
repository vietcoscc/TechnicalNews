package com.example.vaio.technicalnews.model;

import java.io.Serializable;

/**
 * Created by vaio on 21/03/2017.
 */

public class ChildForumItem implements Serializable {

    private String name;
    private String topicNumber;
    private String postNumber;

    public ChildForumItem() {
    }

    public ChildForumItem(String name, String topicNumber, String postNumber) {
        this.name = name;
        this.topicNumber = topicNumber;
        this.postNumber = postNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(String topicNumber) {
        this.topicNumber = topicNumber;
    }

    public String getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(String postNumber) {
        this.postNumber = postNumber;
    }
}
