package com.example.vaio.technicalnews.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.chat.ChatAdapter;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.Emoji;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.application.MyCalendar;
import com.example.vaio.technicalnews.model.chat.ChatRoom;
import com.example.vaio.technicalnews.model.chat.ItemChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.vaio.technicalnews.model.application.FireBaseReference.CHAT_ROOM;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.getRoomChatRef;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";
    private EditText edtComment;
    private ImageView ibSend;
    private RecyclerView recyclerView;
    private int position;
    private String key;
    private ChatRoom chatRoom;
    private ArrayList<ItemChat> arrChat = new ArrayList<>();
    private ArrayList<String> arrBan = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        try {
            initAccountManager();
            initToolbar();
            initData();
            initComponent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAccountManager() throws Exception {
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
//        arrBan = globalData.getArrBan();
        receiveBanList();
    }

    private void initData() throws Exception {
        chatRoom = (ChatRoom) getIntent().getExtras().getSerializable(CHAT_ROOM);
        Toast.makeText(this, chatRoom.getName(), Toast.LENGTH_SHORT).show();
        receiveData();
    }

    private void receiveBanList() {
        arrBan.clear();
        FireBaseReference.getBanRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String mail = dataSnapshot.getValue(String.class);
                if (!(arrBan.indexOf(mail) > -1)) {
                    arrBan.add(mail);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String mail = dataSnapshot.getValue(String.class);
//                arrBan.remove(arrBan.indexOf(mail));
                Log.e(TAG, mail);
                Log.e(TAG, "REmoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void receiveData() {
        arrChat.clear();
        FireBaseReference.getRoomChatRef(chatRoom.getName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ItemChat itemChat = dataSnapshot.getValue(ItemChat.class);
                itemChat.setKey(dataSnapshot.getKey());
                arrChat.add(itemChat);
                chatAdapter.notifyItemInserted(arrChat.size() - 1);
                recyclerView.scrollToPosition(arrChat.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() throws Exception {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initComponent() throws Exception {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatAdapter = new ChatAdapter(arrChat, this, accountManager);
        chatAdapter.setOnItemLongClick(new ChatAdapter.OnItemLongClick() {
            @Override
            public void onLongLick(View view, final int position) {
                Log.e(TAG, position + "");
                if (accountManager.getCurrentUser() == null || !accountManager.getCurrentUser().getUid().equals(arrChat.get(position).getUid())) {
                    return;
                }
                final ItemChat chat = arrChat.get(position);
                PopupMenu popupMenu = new PopupMenu(ChatActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_topic_more, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete:
                                getRoomChatRef(chatRoom.getName()).child(chat.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        arrChat.remove(position);
                                        chatAdapter.notifyItemRemoved(position);
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        recyclerView.setAdapter(chatAdapter);
        edtComment = (EditText) findViewById(R.id.edtComment);
        edtComment.setBackground(null);
        ibSend = (ImageView) findViewById(R.id.ibSend);
        ibSend.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibSend:
                if (arrBan.indexOf(accountManager.getCurrentUser().getEmail()) > -1) {
                    Snackbar.make(v, "You have been banned ! ", 1000).show();
                    return;
                }
                final String chat = Emoji.replaceInText(edtComment.getText().toString()).trim();
                if (!chat.isEmpty()) {
                    String date = MyCalendar.getDate();
                    String time = MyCalendar.getTimeStamp();
                    ItemChat itemChat = new ItemChat(accountManager.getCurrentUser().getUid(), chat, date, time);
                    FireBaseReference.getRoomChatRef(chatRoom.getName()).push().setValue(itemChat);
                    edtComment.setText("");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
