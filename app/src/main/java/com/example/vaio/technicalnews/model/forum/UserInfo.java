package com.example.vaio.technicalnews.model.forum;

/**
 * Created by vaioon 10/04/2017.
 */

public class UserInfo {
    String uid;
    String displayName;
    String email;
    String photoUrl;
    String joinedDate;

    boolean isAdmin;
    boolean isBanned;

    public UserInfo() {
    }

    public UserInfo(String uid, String displayName, String email, String photoUrl, String joinedDate, boolean isAdmin, boolean isBanned) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.joinedDate = joinedDate;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }
}
