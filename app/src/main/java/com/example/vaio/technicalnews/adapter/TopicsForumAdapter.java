package com.example.vaio.technicalnews.adapter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.ForumFragment;
import com.example.vaio.technicalnews.model.Topic;

import java.util.ArrayList;

/**
 * Created by vaio on 1/2/2017.
 */

public class TopicsForumAdapter extends RecyclerView.Adapter<TopicsForumAdapter.ViewHolder> {
    private ArrayList<Topic> arrTopic;
    private ClickListener clickListener;
    private Handler handlerContentLoadingCompletely;

    public TopicsForumAdapter(ArrayList<Topic> arrTopic, Handler handlerContentLoadingCompletely) {
        this.arrTopic = arrTopic;
        this.handlerContentLoadingCompletely = handlerContentLoadingCompletely;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_toptic_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Topic topic = arrTopic.get(arrTopic.size() - position - 1);
        holder.tvEmail.setText(topic.getName());
        holder.tvSubject.setText(topic.getContent().toString());
        holder.tvDate.setText(topic.getDate().toString());
        holder.tvTime.setText(topic.getTime().toString());
        if (position >= arrTopic.size() - 1) {
            Message message = new Message();
            message.what = ForumFragment.WHAT_COMPLETELY;
            handlerContentLoadingCompletely.sendMessage(message);
        }
    }


    @Override
    public int getItemCount() {
        return arrTopic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvEmail;
        TextView tvSubject;
        TextView tvDate;
        TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
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
