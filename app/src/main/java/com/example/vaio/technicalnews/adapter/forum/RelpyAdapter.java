package com.example.vaio.technicalnews.adapter.forum;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.forum.Comment;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nguyễn Quốc Việt on 28/05/2017.
 */

public class RelpyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RelpyAdapter";
    private Context context;
    private ArrayList<Comment> arrReply;
    private AccountManager accountManager;

    public RelpyAdapter(Context context, ArrayList<Comment> arrReply, AccountManager accountManager) {
        this.context = context;
        this.arrReply = arrReply;
        this.accountManager = accountManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply_recycler_view, parent, false);

        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
        final Comment comment = arrReply.get(position);
        replyViewHolder.tvComment.setText(comment.getComment());
        replyViewHolder.tvDate.setText(comment.getDate());
        replyViewHolder.tvTimeStamp.setText(comment.getTime());
        FireBaseReference.getUserIdRef(comment.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, dataSnapshot.getKey());
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                if (userInfo != null) {
                    if (userInfo.getPhotoUrl().isEmpty()) {
                        Picasso.with(context).
                                load(comment.getUid()).
                                placeholder(R.drawable.loading).
                                error(R.drawable.warning).
                                into(replyViewHolder.ivAvatar);

                    } else {
                        Picasso.with(context).
                                load(userInfo.getPhotoUrl()).
                                placeholder(R.drawable.loading).
                                error(R.drawable.warning).
                                into((replyViewHolder.ivAvatar));
                    }
                    replyViewHolder.tvName.setText(userInfo.getDisplayName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrReply.size();
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;
        ImageView ivAvatar;
        TextView tvDate;
        TextView tvTimeStamp;
        TextView tvName;
        EditText edtReply;


        public ReplyViewHolder(View itemView) {
            super(itemView);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            edtReply = (EditText) itemView.findViewById(R.id.edtReply);

        }
    }
}
