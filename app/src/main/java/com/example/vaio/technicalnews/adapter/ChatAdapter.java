package com.example.vaio.technicalnews.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ItemChat;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vaio on 16/03/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";
    private ArrayList<ItemChat> arrChat;
    private Context context;
    private AccountManager accountManager;

    public ChatAdapter(ArrayList<ItemChat> arrChat, Context context, AccountManager accountManager) {
        this.arrChat = arrChat;
        this.context = context;
        this.accountManager = accountManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chat_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ItemChat itemChat = arrChat.get(position);
        holder.tvChat.setText(itemChat.getChat());
        holder.tvName.setText(itemChat.getName());

//        holder.tvDate.setText(itemChat.getDate());
//
//        holder.tvTimeStamp.setText(itemChat.getTime());
        if (itemChat.getUid().equals(accountManager.getCurrentUser().getUid())) {
            Picasso.with(context).
                    load(accountManager.getCurrentUser().getPhotoUrl()).
                    into(holder.circleImageView);
        } else {
            Picasso.with(context).
                    load(itemChat.getUri()).
                    into(holder.circleImageView);
        }

    }

    @Override
    public int getItemCount() {
        return arrChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView tvName;
        TextView tvChat;
        TextView tvDate;
        TextView tvTimeStamp;
        CircleImageView circleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvChat = (TextView) itemView.findViewById(R.id.tvChat);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClick != null) {
                onItemLongClick.onLongLick(v, getPosition());
            }
            return true;
        }
    }

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    private OnItemLongClick onItemLongClick;

    public interface OnItemLongClick {
        void onLongLick(View view, int position);
    }
}
