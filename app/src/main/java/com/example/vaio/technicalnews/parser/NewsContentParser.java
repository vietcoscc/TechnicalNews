package com.example.vaio.technicalnews.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.fragment.NewsFragment;
import com.example.vaio.technicalnews.model.NewsItem;
import com.example.vaio.technicalnews.model.Topic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by vaio on 1/26/2017.
 */

public class NewsContentParser extends AsyncTask<String, Void, ArrayList<NewsItem>> {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    private static final String TAG = "NewsContentParser";
    private Context context;
    private Handler handlerReceiveData;

    public NewsContentParser(Context context, Handler handlerReceiveData) {
        this.context = context;
        this.handlerReceiveData = handlerReceiveData;
    }

    @Override
    protected ArrayList<NewsItem> doInBackground(String... params) {
        ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(params[0])
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();
            Elements elements = doc.select("div.fdListingContainer").select("div.riverPost");
            for (int i = 0; i < elements.size(); i++) {
                if (!MainActivity.isNetWorkAvailable(context)) {
                    break;
                }
                Element elementRoot = elements.get(i);

                String name = elementRoot.select("a.assetHed").select("h3").text().trim();

                String contentPreview = elementRoot.select("a.assetHed").select("p").text().trim();

                String timeStamp = elementRoot.select("div.timestamp").select("div.timeAgo").select("span").text().trim();

                String contentLink = "http://cnet.com" + elementRoot.select("a.imageLinkWrapper").attr("href").trim();

                String imageLinkWrapper = elementRoot.select("a.imageLinkWrapper").select("img").attr("data-original").trim();

                String topicName = elementRoot.select("a.topicName").text().trim();

                String author = elementRoot.select("span.assetAuthor").text().trim();

                NewsItem newsItem = new NewsItem(name, contentPreview, timeStamp, contentLink, imageLinkWrapper, topicName, author);

                arrNewsItem.add(newsItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrNewsItem;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsItem> newsItems) {
        super.onPostExecute(newsItems);
        Message message = new Message();
        message.what = NewsFragment.WHAT_RECEIVE_DATA;
        message.obj = newsItems;
        if (!newsItems.isEmpty()) {
            message.arg1 = SUCCESS;
        } else {
            message.arg1 = FAIL;
        }
        handlerReceiveData.sendMessage(message);
    }
}
