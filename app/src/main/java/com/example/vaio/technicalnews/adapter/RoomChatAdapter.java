package com.example.vaio.technicalnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.RoomChat;

import java.util.ArrayList;

/**
 * Created by vaio on 15/03/2017.
 */

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.ViewHolder> {
    private ArrayList<RoomChat> arrRoomChat;
    private AccountManager accountManager;
    public RoomChatAdapter(ArrayList<RoomChat> arrRoomChat, AccountManager accountManager) {
        this.arrRoomChat = arrRoomChat;
        this.accountManager = accountManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_room_chat_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return arrRoomChat.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvArea.setText(arrRoomChat.get(position).getArea());
//        holder.tvOnlineNumber.setText(arrRoomChat.get(position).getOnlineNumber() + "");
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvArea;
        TextView tvOnlineNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            tvArea = (TextView) itemView.findViewById(R.id.tvArea);
            tvOnlineNumber = (TextView) itemView.findViewById(R.id.tvOnlineNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClick != null) {
                onItemClick.onClick(v,getPosition());
            }
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    private OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick(View view,int position);
    }
}
