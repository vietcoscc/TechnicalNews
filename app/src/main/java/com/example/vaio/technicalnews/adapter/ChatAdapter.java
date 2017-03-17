package com.example.vaio.technicalnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.ItemChat;
import com.example.vaio.technicalnews.model.MyCalendar;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by vaio on 16/03/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<ItemChat> arrChat;

    public ChatAdapter(ArrayList<ItemChat> arrChat) {
        this.arrChat = arrChat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chat_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvChat.setText(arrChat.get(position).getChat());
        holder.tvName.setText(arrChat.get(position).getName());

        holder.tvDate.setText(arrChat.get(position).getDate());

        holder.tvTimeStamp.setText(arrChat.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return arrChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvChat;
        TextView tvDate;
        TextView tvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvChat = (TextView) itemView.findViewById(R.id.tvChat);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
        }
    }
}
