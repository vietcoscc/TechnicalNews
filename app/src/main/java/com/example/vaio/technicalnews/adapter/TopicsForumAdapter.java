package com.example.vaio.technicalnews.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.FireBaseReference;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.Topic;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vaio on 1/2/2017.
 */

public class TopicsForumAdapter extends RecyclerView.Adapter<TopicsForumAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "TopicsForumAdapter";
    private ArrayList<Topic> arrTopic;
    private AccountManager accountManager;
    private ClickListener clickListener;
    private Context context;

    public TopicsForumAdapter(ArrayList<Topic> arrTopic, AccountManager accountManager) {
        this.arrTopic = arrTopic;
        this.accountManager = accountManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_toptic_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Topic topic = arrTopic.get(arrTopic.size() - position - 1);
        Log.e(TAG, topic.getPhotoPath());
        if (accountManager.getCurrentUser() != null && accountManager.getCurrentUser().getEmail().equals(topic.getMail())) {
            Picasso.with(context).load(accountManager.getCurrentUser().getPhotoUrl()).into(holder.ivAvatar);
        } else {
            Picasso.with(context).load(topic.getPhotoPath()).into(holder.ivAvatar);
        }

        holder.tvEmail.setText(topic.getName());
        holder.tvSubject.setText(topic.getSubject().toString());
        holder.tvDate.setText(topic.getDate().toString());
        holder.tvTime.setText(topic.getTime().toString());
        holder.ivMore.setVisibility(View.INVISIBLE);
        if (position >= arrTopic.size() - 1) {
            if (onCompleteLoading != null) {
                onCompleteLoading.onComplete();
            }
        }
    }


    @Override
    public int getItemCount() {
        return arrTopic.size();
    }

    @Override
    public void onClick(View v) {


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView ivAvatar;
        TextView tvEmail;
        TextView tvSubject;
        TextView tvDate;
        TextView tvTime;
        ImageView ivMore;

        public ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivMore = (ImageView) itemView.findViewById(R.id.ivMore);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(itemView, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Log.e(TAG,accountManager.getCurrentUser().getDisplayName());
            if (onItemLongClick != null && accountManager.isAdmin()) {
                onItemLongClick.onLongLick(v, getPosition());
            }
            return true;
        }
    }


    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnCompleteLoading(OnCompleteLoading onCompleteLoading) {
        this.onCompleteLoading = onCompleteLoading;
    }

    private OnCompleteLoading onCompleteLoading;

    public interface OnCompleteLoading {
        void onComplete();
    }

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    private OnItemLongClick onItemLongClick;

    public interface OnItemLongClick {
        void onLongLick(View view, int position);
    }
}
