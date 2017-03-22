package com.example.vaio.technicalnews.adapter;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.ReviewsFragment;
import com.example.vaio.technicalnews.model.NewsClipItem;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vaio on 27/02/2017.
 */

public class NewsClipHomeAdapter extends RecyclerView.Adapter<NewsClipHomeAdapter.ViewHolder> {
    private ArrayList<NewsClipItem> arrNewsClipItem;
    private FragmentManager fragmentManager;
    private Context context;

    public NewsClipHomeAdapter(Context context, ArrayList<NewsClipItem> arrNewsClipItem) {
        this.arrNewsClipItem = arrNewsClipItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_news_clip_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsClipItem newsClipItem = arrNewsClipItem.get(position);
        Picasso.with(context).
                load(newsClipItem.getImageLink()).
                into(holder.ivImage);
        holder.ivImage.setY(holder.ivImage.getX() * 2 / 3);
        holder.tvTitle.setText(newsClipItem.getTitle());
        holder.tvTimeStamp.setText(newsClipItem.getTimeStamp());
        holder.tvViewNumber.setText(newsClipItem.getViewNumber());
    }

    @Override
    public int getItemCount() {
        return arrNewsClipItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        YouTubePlayerSupportFragment youTubePlayerFragment;
        ImageView ivImage;
        TextView tvTitle;
        TextView tvViewNumber;
        TextView tvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvViewNumber = (TextView) itemView.findViewById(R.id.tvViewNumber);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, getPosition());
            }
        }
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View itemView, int position);
    }
}
