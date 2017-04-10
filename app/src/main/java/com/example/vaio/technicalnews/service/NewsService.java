package com.example.vaio.technicalnews.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.SplashScreenActivity;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.MyCalendar;
import com.example.vaio.technicalnews.model.NewsItem;
import com.example.vaio.technicalnews.parser.NewsContentParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by vaio on 31/03/2017.
 */

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private ArrayList<NewsItem> arrNewsItem1 = new ArrayList<>();
    private ArrayList<NewsItem> arrNewsItem2 = new ArrayList<>();
    private boolean isRunning = true;
    private NewsItem newestItem;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread thread = new Thread(runnable);
        thread.start();
        return START_STICKY;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                while (isRunning) {
//                    Log.e(TAG, MyCalendar.getSecond() + "");
//                NewsContentParser newsContentParser = new NewsContentParser(NewsService.this);
//                newsContentParser.setOnReceiveData(new NewsContentParser.OnReceiveData() {
//                    @Override
//                    public void onReceive(ArrayList<NewsItem> arrNewsItem) {
//                        arrNewsItem1.clear();
//                        arrNewsItem1.addAll(arrNewsItem);
//                        if (arrNewsItem1.isEmpty() || arrNewsItem2.isEmpty()) {
//                            arrNewsItem2.addAll(arrNewsItem);
//                            return;
//                        } else {
//                            NewsItem newsItem1 = arrNewsItem1.get(0);
//                            NewsItem newsItem2 = arrNewsItem2.get(0);
//                            if (!newsItem1.getContentLink().equals(newsItem2.getContentLink())) {
//
//                                Intent intent = new Intent(NewsService.this, SplashScreenActivity.class);
//                                PendingIntent pending = PendingIntent.getActivity(NewsService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                NotificationCompat.Builder build = (NotificationCompat.Builder) new NotificationCompat.Builder(NewsService.this).
//                                        setSmallIcon(R.drawable.news).
//                                        setContentTitle(newsItem1.getName()).
//                                        setContentText(newsItem1.getContentPreview()).
//                                        setContentIntent(pending);
//
//                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                manager.notify(MyCalendar.getSecond(), build.build());
//                                arrNewsItem2.clear();
//                                arrNewsItem2.addAll(arrNewsItem1);
//                            }
//                        }
//                    }
//                });
                    Document doc = Jsoup.connect("https://www.cnet.com/news/")
                            .userAgent("Mozilla")
                            .timeout(5000)
                            .get();
                    Elements elements = doc.select("div.fdListingContainer").select("div.riverPost");
                    Element elementRoot = elements.get(0);
                    String name = elementRoot.select("a.assetHed").select("h3").text().trim();
                    String contentPreview = elementRoot.select("a.assetHed").select("p").text().trim();
                    String timeStamp = elementRoot.select("div.timestamp").select("div.timeAgo").select("span").text().trim();
                    String contentLink = "http://cnet.com" + elementRoot.select("a.imageLinkWrapper").attr("href").trim();
                    String imageLinkWrapper = elementRoot.select("a.imageLinkWrapper").select("img").attr("data-original").trim();
                    String topicName = elementRoot.select("a.topicName").text().trim();
                    String author = elementRoot.select("span.assetAuthor").text().trim();
                    NewsItem newsItem = new NewsItem(name, contentPreview, timeStamp, contentLink, imageLinkWrapper, topicName, author);
                    if (newestItem == null) {
                        newestItem = newsItem;
                    } else {
                        if (!newestItem.getContentLink().equals(newsItem.getContentLink())) {
                            Intent intent = new Intent(NewsService.this, SplashScreenActivity.class);
                            PendingIntent pending = PendingIntent.getActivity(NewsService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder build = (NotificationCompat.Builder) new NotificationCompat.Builder(NewsService.this).
                                    setSmallIcon(R.drawable.news).
                                    setContentTitle(newestItem.getName()).
                                    setContentText(newestItem.getContentPreview()).
                                    setContentIntent(pending);

                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            manager.notify(MyCalendar.getSecond(), build.build());
                            newestItem = newsItem;

                        } else {

                        }
                    }
                    Thread.sleep(300000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
