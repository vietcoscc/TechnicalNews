package com.example.vaio.technicalnews.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.fragment.ReviewsFragment;
import com.example.vaio.technicalnews.model.NewsClipItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by vaio on 27/02/2017.
 */

public class NewsClipParser extends AsyncTask<String, Void, ArrayList<NewsClipItem>> {
    public static final String TAG = "NewsClipParser";
    private Context context;
    private Handler handlerReceiveData;

    public NewsClipParser(Context context, Handler handlerReceiveData) {
        this.handlerReceiveData = handlerReceiveData;
        this.context = context;
    }

    @Override
    protected ArrayList<NewsClipItem> doInBackground(String... params) {
        ArrayList<NewsClipItem> arrNewsClip = new ArrayList<>();
        try {

            String link = params[0];
            Document document = Jsoup.connect(link).timeout(10000).userAgent("Mozilla").get();
            Elements elementsNewsClip = document.select("div.yt-lockup-dismissable");
            for (int i = 0; i < elementsNewsClip.size(); i++) {
                if (!MainActivity.isNetWorkAvailable(context)) {
                    break;
                }
                Element element = elementsNewsClip.get(i);
                String imageLink = element.select("span.yt-thumb-clip").select("img").attr("src");
                String title = element.select("h3.yt-lockup-title").select("a").text();
                Elements elementsLockUpMetaInfo = element.select("ul.yt-lockup-meta-info").select("li");
                String viewNumber = elementsLockUpMetaInfo.get(0).text();
                String timeStamp = elementsLockUpMetaInfo.get(1).text();
                String clipLink = element.select("a.yt-uix-sessionlink").attr("href").substring(9);

                Log.e(TAG, imageLink);
                Log.e(TAG, title);
                Log.e(TAG, viewNumber);
                Log.e(TAG, timeStamp);
                Log.e(TAG, clipLink);

                arrNewsClip.add(new NewsClipItem(imageLink, title, viewNumber, timeStamp, clipLink));
            }
            Log.e(TAG, elementsNewsClip.size() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrNewsClip;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsClipItem> newsClipItems) {
        super.onPostExecute(newsClipItems);
        Message message = new Message();
        message.what = ReviewsFragment.WHAT_RECEIVE_NEWS_CLIP_DATA;
        message.obj = newsClipItems;
        handlerReceiveData.sendMessage(message);

    }
}
