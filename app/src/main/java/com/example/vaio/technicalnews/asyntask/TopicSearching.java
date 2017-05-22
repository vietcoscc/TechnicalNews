package com.example.vaio.technicalnews.asyntask;

import android.os.AsyncTask;

import com.example.vaio.technicalnews.model.forum.Topic;

import java.util.ArrayList;

/**
 * Created by vaio on 14/05/2017.
 */

public class TopicSearching extends AsyncTask<ArrayList<Topic>, Void, ArrayList<Topic>> {
    private String text;

    public TopicSearching(String text) {
        this.text = text;
    }

    @Override
    protected ArrayList<Topic> doInBackground(ArrayList<Topic>... params) {
        ArrayList<Topic> arrTopic = params[0];
        ArrayList<Topic> arrTopicTmp = new ArrayList<>();
        int n = arrTopic.size();
        for (int i = 0; i < n; i++) {
            if (arrTopic.get(i).toString().contains(text)) {
                arrTopicTmp.add(arrTopic.get(i));
            }
        }
        return arrTopicTmp;
    }

    @Override
    protected void onPostExecute(ArrayList<Topic> topics) {
        super.onPostExecute(topics);
        if (onSearchingComplete != null) {
            onSearchingComplete.onComplete(topics);
        }
    }

    public interface OnSearchingComplete {
        void onComplete(ArrayList<Topic> arrTopic);
    }

    private OnSearchingComplete onSearchingComplete;

    public void setOnSearchingComplete(OnSearchingComplete onSearchingComplete) {
        this.onSearchingComplete = onSearchingComplete;
    }
}
