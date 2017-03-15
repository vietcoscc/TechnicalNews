package com.example.vaio.technicalnews.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaio on 27/02/2017.
 */

public class NewsClipItem implements Parcelable{
    private String imageLink;
    private String title;
    private String viewNumber;
    private String timeStamp;
    private String clipLink;

    public NewsClipItem(String imageLink, String title, String viewNumber, String timeStamp, String clipLink) {
        this.imageLink = imageLink;
        this.title = title;
        this.viewNumber = viewNumber;
        this.timeStamp = timeStamp;
        this.clipLink = clipLink;
    }

    protected NewsClipItem(Parcel in) {
        imageLink = in.readString();
        title = in.readString();
        viewNumber = in.readString();
        timeStamp = in.readString();
        clipLink = in.readString();
    }

    public static final Creator<NewsClipItem> CREATOR = new Creator<NewsClipItem>() {
        @Override
        public NewsClipItem createFromParcel(Parcel in) {
            return new NewsClipItem(in);
        }

        @Override
        public NewsClipItem[] newArray(int size) {
            return new NewsClipItem[size];
        }
    };

    public String getImageLink() {
        return imageLink;
    }

    public String getTitle() {
        return title;
    }

    public String getViewNumber() {
        return viewNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getClipLink() {
        return clipLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageLink);
        dest.writeString(title);
        dest.writeString(viewNumber);
        dest.writeString(timeStamp);
        dest.writeString(clipLink);
    }
}
