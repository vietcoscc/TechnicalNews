package com.example.vaio.technicalnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.Emoji;
import com.example.vaio.technicalnews.model.GroupForumItem;
import com.example.vaio.technicalnews.model.Topic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vaio on 23/03/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_COMMENT = 1;
    private static final String TAG = "CommentAdapter";

    private Topic topic;
    private Context context;
    private AccountManager accountManager;

    public CommentAdapter(Topic topic, AccountManager accountManager) {
        this.accountManager = accountManager;
        this.topic = topic;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_recycler_view, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            Log.e(TAG, topic.getPhotoPath());
            if (topic.getMail().equals(accountManager.getCurrentUser().getEmail())) {
                Picasso.with(context).load(accountManager.getCurrentUser().getPhotoUrl()).into(headerViewHolder.ivAvatar);
            } else {
                Picasso.with(context).load(topic.getPhotoPath()).into(headerViewHolder.ivAvatar);
            }

            headerViewHolder.tvContent.setText(Emoji.replaceInText(topic.getContent()));
            headerViewHolder.tvSubject.setText(Emoji.replaceInText(topic.getSubject()));
            headerViewHolder.tvName.setText(topic.getName());
            headerViewHolder.tvTimeStamp.setText(topic.getTime());
            headerViewHolder.tvDate.setText(topic.getDate());
        } else {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            Picasso.with(context).load(topic.getArrComment().get(position - 1).getPhotoPath()).into(commentViewHolder.ivAvatar);
            commentViewHolder.tvComment.setText(topic.getArrComment().get(position - 1).getComment());
            commentViewHolder.tvName.setText(topic.getArrComment().get(position - 1).getName());
            commentViewHolder.tvDate.setText(topic.getArrComment().get(position - 1).getDate());
            commentViewHolder.tvTimeStamp.setText(topic.getArrComment().get(position - 1).getTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_COMMENT;
        }
    }

    @Override
    public int getItemCount() {
        if (topic.getArrComment() == null) {
            return 1;
        } else {
            return topic.getArrComment().size() + 1;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvSubject;
        TextView tvContent;
        TextView tvTimeStamp;
        TextView tvDate;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView tvComment;
        ImageView ivAvatar;
        TextView tvDate;
        TextView tvTimeStamp;
        TextView tvName;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClick != null) {
                onItemLongClick.onLongClick(v, getPosition());
            }
            return true;
        }
    }

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    private OnItemLongClick onItemLongClick;

    public interface OnItemLongClick {
        void onLongClick(View view, int position);
    }
}
