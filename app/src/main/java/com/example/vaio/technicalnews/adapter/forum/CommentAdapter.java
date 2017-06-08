package com.example.vaio.technicalnews.adapter.forum;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.asyntask.FetchImageUrl;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.MyCalendar;
import com.example.vaio.technicalnews.model.forum.Comment;
import com.example.vaio.technicalnews.model.forum.Topic;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

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
    private AnimationSet animScaleOut;
    private AnimationSet animScaleIn;

    public CommentAdapter(Topic topic, AccountManager accountManager) {
        this.accountManager = accountManager;
        this.topic = topic;
        arrFavorite = topic.getArrFavorite();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        animScaleOut = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_scale_out_view);
        animScaleIn = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_scale_in_view);
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

    public Html.ImageGetter getImageHTML() {
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String urlString) {
                try {
                    Log.d(TAG, "getDrawable: " + urlString);
                    FetchImageUrl f = new FetchImageUrl(context);
                    f.execute(urlString).get();
                    Drawable drawable = f.getDrawable();
                    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    int x = windowManager.getDefaultDisplay().getWidth();

                    drawable.setBounds(0, 0, x, x * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                    return drawable;
                } catch (Exception e) {
                    e.printStackTrace();
                    return context.getResources().getDrawable(R.drawable.boss);
                }
            }
        };
        return imageGetter;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {
            arrFavorite = topic.getArrFavorite();
            final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
//            headerViewHolder.tvContent.setText(Emoji.replaceInText(topic.getContent()));
            Spanned spanned = Html.fromHtml(Emoji.replaceInText(topic.getContent()), getImageHTML(), null);
            headerViewHolder.tvContent.setText(spanned);
            headerViewHolder.tvSubject.setText(Emoji.replaceInText(topic.getSubject()));
            headerViewHolder.tvTimeStamp.setText(topic.getTime());
            headerViewHolder.tvDate.setText(topic.getDate());
            headerViewHolder.tvfavorite.setText((arrFavorite.size()-1) + "");

            if (arrFavorite != null && arrFavorite.indexOf(accountManager.getCurrentUser().getUid()) > -1) {
                isFavorited = true;
                headerViewHolder.btnFavorite.setActivated(true);
            } else {
                isFavorited = false;
                headerViewHolder.btnFavorite.setActivated(false);
            }
            headerViewHolder.btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    headerViewHolder.btnFavorite.setActivated(!headerViewHolder.btnFavorite.isActivated());
                    final ArrayList<String> arrFavorite = topic.getArrFavorite();

                    if (isFavorited) {
                        if (arrFavorite != null) {
                            FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).
                                    child(arrFavorite.indexOf(uid) + "").removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    FireBaseReference.getNumberCareRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).setValue(arrFavorite.size() - 1);
                                }
                            });
                        } else {

                        }
                        isFavorited = false;
                    } else {
                        if (arrFavorite != null) {
                            if (arrFavorite.indexOf(uid) == -1) {
                                FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).child(topic.getArrFavorite().size() + "").setValue(uid);
                            }
                        } else {
                            FireBaseReference.getArrFavoriteRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).child("0").setValue(uid);
                        }
                        FireBaseReference.getNumberCareRef(topic.getGroupName(), topic.getChildName(), topic.getKey()).setValue(arrFavorite.size() + 1);
                        isFavorited = true;
                    }

                }
            });
            headerViewHolder.layoutFavorite.setOnClickListener(new View.OnClickListener() {
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
            final int positionComment = position - 1;
            final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            final Comment comment = topic.getArrComment().get(positionComment);
            commentViewHolder.tvComment.setText(comment.getComment());
            commentViewHolder.tvDate.setText(comment.getDate());
            commentViewHolder.tvTimeStamp.setText(comment.getTime());
            //
            final ArrayList<Comment> arrReply = comment.getArrReply();
            final RelpyAdapter relpyAdapter = new RelpyAdapter(context, arrReply, accountManager);
            commentViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            commentViewHolder.recyclerView.setAdapter(relpyAdapter);
            //
            commentViewHolder.ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentViewHolder.edtReply.getVisibility() == View.GONE) {
                        commentViewHolder.edtReply.setVisibility(View.VISIBLE);
                        commentViewHolder.ibSend.setVisibility(View.VISIBLE);
                    } else {
                        commentViewHolder.edtReply.setVisibility(View.GONE);
                        commentViewHolder.ibSend.setVisibility(View.GONE);
                    }
                }
            });
            commentViewHolder.layoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null) {
                        onItemClick.onClick(v, position);
                    }
                    if (commentViewHolder.layoutReply.getVisibility() == View.VISIBLE) {
                        animScaleIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                commentViewHolder.layoutReply.setVisibility(View.GONE);
                                commentViewHolder.layoutDateTime.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        commentViewHolder.layoutReply.startAnimation(animScaleIn);


                    } else {
                        commentViewHolder.layoutReply.startAnimation(animScaleOut);
                        commentViewHolder.layoutReply.setVisibility(View.VISIBLE);
                        commentViewHolder.layoutDateTime.setVisibility(View.VISIBLE);
                    }
                }
            });
            commentViewHolder.ibSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();

                    final String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "";
                    String time;
                    if (calendar.get(Calendar.AM_PM) == 1) {
                        time = MyCalendar.getTimeStamp() + " PM";
                    } else {
                        time = MyCalendar.getTimeStamp() + " AM";
                    }
                    String reply = commentViewHolder.edtReply.getText().toString();
                    commentViewHolder.edtReply.getText().clear();
                    final Comment comment1 = new Comment(uid, reply, date, time, null);
                    FireBaseReference.getArrReplyRef(topic.getGroupName(), topic.getChildName(), topic.getKey(), positionComment + "").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e(TAG, dataSnapshot.getChildrenCount() + "");
                            FireBaseReference.getArrReplyRef(topic.getGroupName(), topic.getChildName(), topic.getKey(), positionComment + "").
                                    child(dataSnapshot.getChildrenCount() + "").
                                    setValue(comment1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
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
        ImageView btnFavorite;
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
            btnFavorite = (ImageView) itemView.findViewById(R.id.btnFavorite);
            tvfavorite = (TextView) itemView.findViewById(R.id.tvFavorite);
            layoutFavorite = (LinearLayout) itemView.findViewById(R.id.layoutFavorite);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView tvComment;
        ImageView ivAvatar;
        TextView tvDate;
        TextView tvTimeStamp;
        TextView tvName;
        RecyclerView recyclerView;
        ImageButton ibSend;
        LinearLayout layoutReply;
        LinearLayout layoutDateTime;
        LinearLayout layoutComment;
        EditText edtReply;
        ImageView ivReply;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            ibSend = (ImageButton) itemView.findViewById(R.id.ibSend);
            layoutReply = (LinearLayout) itemView.findViewById(R.id.layoutReply);
            layoutDateTime = (LinearLayout) itemView.findViewById(R.id.layoutDateTime);
            layoutComment = (LinearLayout) itemView.findViewById(R.id.layoutComment);
            edtReply = (EditText) itemView.findViewById(R.id.edtReply);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClick != null) {
                onItemLongClick.onLongClick(v, getPosition());
            }
            return true;
        }

        @Override
        public void onClick(View v) {
            if (onItemClick != null) {
                onItemClick.onClick(v, getPosition());
            }

        }
    }

    public interface OnReplyListener {
        void onNotify(RelpyAdapter relpyAdapter, int position);
    }

    public OnReplyListener onReplyListener;

    public void setOnReplyListener(OnReplyListener onReplyListener) {
        this.onReplyListener = onReplyListener;
    }

    public void setOnItemLongClick(OnItemLongClick onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    private OnItemLongClick onItemLongClick;
    private OnItemClick onItemClick;

    public interface OnItemClick {
        void onClick(View view, int positon);
    }

    public interface OnItemLongClick {
        void onLongClick(View view, int position);
    }
}
