package com.example.vaio.technicalnews.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.vaio.technicalnews.model.news.NewsItem;

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

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(ID));
            String contentPreview = cursor.getString(cursor.getColumnIndex(CONTENT_PREVIEW));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String timeStamp = cursor.getString(cursor.getColumnIndex(TIME_STAMP));
            String contentLink = cursor.getString(cursor.getColumnIndex(CONTENT_LINK));
            String imageLinkWrapper = cursor.getString(cursor.getColumnIndex(IMAGE_LINK_WRAPPER));
            String topicName = cursor.getString(cursor.getColumnIndex(TOPIC_NAME));
            String author = cursor.getString(cursor.getColumnIndex(AUTHOR));

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
