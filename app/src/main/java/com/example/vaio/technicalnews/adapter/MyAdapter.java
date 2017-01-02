package com.example.vaio.technicalnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.Topic;

import java.util.ArrayList;

/**
 * Created by vaio on 1/2/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Topic> arrTopic;
    private ClickListener clickListener;

    public MyAdapter(ArrayList<Topic> arrTopic) {
        this.arrTopic = arrTopic;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic topic = arrTopic.get(position);
        holder.tvEmail.setText(topic.getMail().toString());
        holder.tvContent.setText(topic.getContent().toString());
        holder.tvDate.setText(topic.getDate().toString());
        holder.tvTime.setText(topic.getTime().toString());
    }


    @Override
    public int getItemCount() {
        return arrTopic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEmail;
        TextView tvContent;
        TextView tvDate;
        TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(itemView, getPosition());
            }
        }
    }


    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
