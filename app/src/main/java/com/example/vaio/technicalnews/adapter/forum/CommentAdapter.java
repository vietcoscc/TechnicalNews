package com.example.vaio.technicalnews.adapter.forum;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

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
    private Uri uri;
    private boolean isFavorited = true;
    private String uid;
    private ArrayList<String> arrFavorite;

    private Drawable drawableFavorite;
    private Drawable drawableUnFavorite;

    public CommentAdapter(Topic topic, AccountManager accountManager) {
        this.accountManager = accountManager;
        this.topic = topic;
        arrFavorite = topic.getArrFavorite();


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        drawableFavorite = ContextCompat.getDrawable(context, R.drawable.ic_favorited);
        drawableUnFavorite = ContextCompat.getDrawable(context, R.drawable.ic_favortite);
        drawableFavorite.setBounds(0, 0, 48, 48);
        drawableUnFavorite.setBounds(0, 0, 48, 48);

        this.uri = MainActivity.getUriToDrawable(context, R.drawable.boss);
        this.uid = accountManager.getCurrentUser().getUid();

        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_header_recycler_view, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_recycler_view, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvContent.setText(Emoji.replaceInText(topic.getContent()));
            headerViewHolder.tvSubject.setText(Emoji.replaceInText(topic.getSubject()));
            headerViewHolder.tvTimeStamp.setText(topic.getTime());
            headerViewHolder.tvDate.setText(topic.getDate());
            headerViewHolder.tvfavorite.setText(arrFavorite.size() + "");

            if (arrFavorite != null && arrFavorite.indexOf(accountManager.getCurrentUser().getUid()) > -1) {
                isFavorited = true;
                headerViewHolder.btnFavorite.setCompoundDrawables(null, null, null, drawableFavorite);
            } else {
                isFavorited = false;
                headerViewHolder.btnFavorite.setCompoundDrawables(null, null, null, drawableUnFavorite);
            }

            if (topic.getArrComment() != null) {
                headerViewHolder.tvCommentNumber.setText(topic.getArrComment().size() + "");
            }
            headerViewHolder.tvViewNumber.setText(topic.getNumberView() + "");

            FireBaseReference.getUserIdRef(topic.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, dataSnapshot.getKey());
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                    if (userInfo != null) {
                        if (userInfo.getPhotoUrl().isEmpty()) {
                            Picasso.with(context).
                                    load(uri).
                                    placeholder(R.drawable.loading).
                                    error(R.drawable.warning).
                                    into(((HeaderViewHolder) holder).ivAvatar);

                        } else {
                            Picasso.with(context).
                                    load(userInfo.getPhotoUrl()).
                                    placeholder(R.drawable.loading).
                                    error(R.drawable.warning).
                                    into(((HeaderViewHolder) holder).ivAvatar);
                        }
                        ((HeaderViewHolder) holder).tvName.setText(userInfo.getDisplayName());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {

            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.tvComment.setText(topic.getArrComment().get(position - 1).getComment());
            commentViewHolder.tvDate.setText(topic.getArrComment().get(position - 1).getDate());
            commentViewHolder.tvTimeStamp.setText(topic.getArrComment().get(position - 1).getTime());

            FireBaseReference.getUserIdRef(topic.getArrComment().get(position - 1).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, dataSnapshot.getKey());
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                    if (userInfo != null) {
                        if (userInfo.getPhotoUrl().isEmpty()) {
                            Picasso.with(context).
                                    load(uri).
                                    placeholder(R.drawable.loading).
                                    error(R.drawable.warning).
                                    into(((CommentViewHolder) holder).ivAvatar);

                        } else {
                            Picasso.with(context).
                                    load(userInfo.getPhotoUrl()).
                                    placeholder(R.drawable.loading).
                                    error(R.drawable.warning).
                                    into(((CommentViewHolder) holder).ivAvatar);
                        }
                        ((CommentViewHolder) holder).tvName.setText(userInfo.getDisplayName());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
        TextView tvViewNumber;
        TextView tvCommentNumber;
        FButton btnFavorite;
        TextView tvfavorite;
        LinearLayout layoutFavorite;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvViewNumber = (TextView) itemView.findViewById(R.id.tvViewNumber);
            tvCommentNumber = (TextView) itemView.findViewById(R.id.tvCommentNumber);
            btnFavorite = (FButton) itemView.findViewById(R.id.btnFavorite);
            tvfavorite = (TextView) itemView.findViewById(R.id.tvFavorite);
            layoutFavorite = (LinearLayout) itemView.findViewById(R.id.layoutFavorite);


            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> arrFavorite = topic.getArrFavorite();

                    if (isFavorited) {
                        if (arrFavorite != null) {
                            FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).
                                    child(arrFavorite.indexOf(uid) + "").removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    FireBaseReference.getNumberCareRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).setValue(arrFavorite.size()-1);
                                }
                            });
                        } else {

                        }
                        btnFavorite.setCompoundDrawables(null, null, null, drawableUnFavorite);
                        isFavorited = false;
                    } else {
                        if (arrFavorite != null) {
                            if (arrFavorite.indexOf(uid) == -1) {
                                FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).child(topic.getArrFavorite().size() + "").setValue(uid);
                            }
                        } else {
                            FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).child("0").setValue(uid);
                        }
                        FireBaseReference.getNumberCareRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).setValue(arrFavorite.size()+1);
                        isFavorited = true;
                        btnFavorite.setCompoundDrawables(null, null, null, drawableFavorite);
                    }
                }
            });

            layoutFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> arrName = new ArrayList<>();
                    final ArrayList<String> arrUid = new ArrayList<>();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                    ListView listView = new ListView(context);
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrName);
                    listView.setAdapter(arrayAdapter);
                    builder.setView(listView);
                    builder.setTitle("Favorited");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    arrFavorite = topic.getArrFavorite();
                    for (int i = 1; i < arrFavorite.size(); i++) {
                        Log.e(TAG, arrFavorite.get(i));
                        FireBaseReference.getAccountRef().child(arrFavorite.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                                if (userInfo != null) {
                                    arrName.add(userInfo.getDisplayName());
                                    arrUid.add(userInfo.getUid());
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
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
