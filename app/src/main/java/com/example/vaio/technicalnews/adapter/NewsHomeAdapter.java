package com.example.vaio.technicalnews.adapter;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.NewsItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vaio on 1/26/2017.
 */

public class NewsHomeAdapter extends RecyclerView.Adapter<NewsHomeAdapter.ViewHolder> {

    private ArrayList<NewsItem> arrNewsItem;
    private Handler handlerContentLoadingMore;
    private Context context;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public NewsHomeAdapter(ArrayList<NewsItem> arrNewsItem, Handler handlerContentLoadingMore) {
        this.arrNewsItem = arrNewsItem;
        this.handlerContentLoadingMore = handlerContentLoadingMore;
    }

    @Override
    public NewsHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_news_recycler_view, parent, false);
        return new NewsHomeAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final NewsItem newsItem = arrNewsItem.get(position);
        if (newsItem != null) {
            Picasso.with(context).load(newsItem.getImageLinkWrapper()).placeholder(R.drawable.loading).error(R.drawable.warning).into(holder.ivWrapper);
            holder.tvName.setText(newsItem.getName());
            holder.tvContentPreview.setText(newsItem.getContentPreview());
            holder.tvTopicName.setText(newsItem.getTopicName());
            holder.tvAuthor.setText(newsItem.getAuthor());
            holder.tvTimeStamp.setText(newsItem.getTimeStamp());
            holder.ivWatchLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

        }
        if (position >= arrNewsItem.size() - 1) {
            Message message = new Message();
            message.what = 1;
            handlerContentLoadingMore.sendMessage(message);
            return;
        }
    }

    @Override
    public int getItemCount() {
        return arrNewsItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivWrapper;
        TextView tvName;
        TextView tvContentPreview;
        TextView tvTopicName;
        TextView tvAuthor;
        TextView tvTimeStamp;
        ImageView ivWatchLater;

        public ViewHolder(View itemView) {
            super(itemView);
            ivWrapper = (ImageView) itemView.findViewById(R.id.ivWrapper);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvContentPreview = (TextView) itemView.findViewById(R.id.tvContentPreview);
            tvTopicName = (TextView) itemView.findViewById(R.id.tvTopicName);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivWatchLater = (ImageView) itemView.findViewById(R.id.watchLater);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(v, getPosition());
            }
        }

    }
}
