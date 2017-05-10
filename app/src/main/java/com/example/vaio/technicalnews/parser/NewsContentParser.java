package com.example.vaio.technicalnews.parser;

import android.content.Context;
import android.os.AsyncTask;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.model.news.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by vaio on 1/26/2017.
 */

public class NewsContentParser extends AsyncTask<String, Void, ArrayList<NewsItem>> {
    private static final String TAG = "NewsContentParser";
    private Context context;

    public NewsContentParser(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<NewsItem> doInBackground(String... params) {
        ArrayList<NewsItem> arrNewsItem = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(params[0])
                    .userAgent("Mozilla")
                    .timeout(5000)
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
        if (onReceiveData != null) {
            onReceiveData.onReceive(newsItems);
        }
    }

    public void setOnReceiveData(OnReceiveData onReceiveData) {
        this.onReceiveData = onReceiveData;
    }

    private OnReceiveData onReceiveData;

    public interface OnReceiveData {
        void onReceive(ArrayList<NewsItem> arrNewsItem);
    }
}
