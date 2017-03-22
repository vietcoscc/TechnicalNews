package com.example.vaio.technicalnews.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 21/03/2017.
 */

public class GroupForumItem implements Serializable {
    private String name;
    private ArrayList<ChildForumItem> arrChildForumItem;

    public GroupForumItem() {
    }

    public GroupForumItem(String name, ArrayList<ChildForumItem> arrChildForumItem) {
        this.name = name;
        this.arrChildForumItem = arrChildForumItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChildForumItem> getArrChildForumItem() {
        return arrChildForumItem;
    }

    public void setArrChildForumItem(ArrayList<ChildForumItem> arrChildForumItem) {
        this.arrChildForumItem = arrChildForumItem;
    }
}
