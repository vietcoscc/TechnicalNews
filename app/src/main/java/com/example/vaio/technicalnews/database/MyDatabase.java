package com.example.vaio.technicalnews.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.vaio.technicalnews.model.NewsItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by vaio on 2/7/2017.
 */

public class MyDatabase {
    public static final String DB_NAME = "technicalnews.sqlite";
    public static final String TB_NAME_NEWS = "NEWS";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String CONTENT_PREVIEW = "CONTENT_PREVIEW";
    public static final String TIME_STAMP = "TIME_STAMP";
    public static final String CONTENT_LINK = "CONTENT_LINK";
    public static final String IMAGE_LINK_WRAPPER = "IMAGE_LINK_WRAPPER";
    public static final String TOPIC_NAME = "TOPIC_NAME";
    public static final String AUTHOR = "AUTHOR";

    public static final String PATH = Environment.getDataDirectory() + "/data/com.example.vaio.technicalnews/databases/" + DB_NAME;
    private Context context;
    private SQLiteDatabase database;

    public MyDatabase(Context context) throws Exception {
        this.context = context;
    }

    public void openDatabase() {
        database = Database.initDatabase((Activity) context, DB_NAME);
    }

    public void closeDatabase() {
        database.close();
    }

    public ArrayList<NewsItem> getArrNewsItem() throws Exception {
        openDatabase();
        ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
        Cursor cursor = database.query(TB_NAME_NEWS, null, null, null, null, null, null);
        int idIndex = cursor.getColumnIndex(ID);
        int nameIndex = cursor.getColumnIndex(NAME);
        int contentPreviewIndex = cursor.getColumnIndex(CONTENT_PREVIEW);
        int timeStampIndex = cursor.getColumnIndex(TIME_STAMP);
        int contentLinkIndex = cursor.getColumnIndex(CONTENT_LINK);
        int imageLinkWrapperIndex = cursor.getColumnIndex(IMAGE_LINK_WRAPPER);
        int topicNameIndex = cursor.getColumnIndex(TOPIC_NAME);
        int authorIndex = cursor.getColumnIndex(AUTHOR);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(idIndex);
            String contentPreview = cursor.getString(contentPreviewIndex);
            String name = cursor.getString(nameIndex);
            String timeStamp = cursor.getString(timeStampIndex);
            String contentLink = cursor.getString(contentLinkIndex);
            String imageLinkWrapper = cursor.getString(imageLinkWrapperIndex);
            String topicName = cursor.getString(topicNameIndex);
            String author = cursor.getString(authorIndex);

            NewsItem newsItem = new NewsItem(name, contentPreview, timeStamp, contentLink, imageLinkWrapper, topicName, author);
            arrNewsItem.add(newsItem);
            cursor.moveToNext();
        }
        closeDatabase();
        return arrNewsItem;
    }

    public void addNewsItem(NewsItem newsItem) throws Exception {
        openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, newsItem.getName());
        contentValues.put(CONTENT_PREVIEW, newsItem.getContentPreview());
        contentValues.put(TIME_STAMP, newsItem.getTimeStamp());
        contentValues.put(CONTENT_LINK, newsItem.getContentLink());
        contentValues.put(IMAGE_LINK_WRAPPER, newsItem.getImageLinkWrapper());
        contentValues.put(TOPIC_NAME, newsItem.getTopicName());
        contentValues.put(AUTHOR, newsItem.getAuthor());

        long rowID = database.insert(TB_NAME_NEWS, null, contentValues);

        closeDatabase();
    }

    public void addArrNewsItem(ArrayList<NewsItem> arrNewsItem) throws Exception {
        for (int i = 0; i < arrNewsItem.size(); i++) {
            addNewsItem(arrNewsItem.get(i));
        }
    }

    public void clearTable(String tableName) throws Exception {
        openDatabase();
        switch (tableName) {
            case TB_NAME_NEWS:
                database.delete(tableName, null, null);
                break;
        }
        closeDatabase();
    }
}
