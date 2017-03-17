package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.ChatAdapter;
import com.example.vaio.technicalnews.fragment.ChatRoomFragment;
import com.example.vaio.technicalnews.model.ItemChat;
import com.example.vaio.technicalnews.model.MyCalendar;
import com.example.vaio.technicalnews.model.RoomChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String CHAT_ROOM = "Chat room";
    private static final String TAG = "ChatActivity";
    private EditText edtComment;
    private ImageButton ibSend;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private RoomChat roomChat;
    private int position;
    private String key;
    private ArrayList<ItemChat> arrChat = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        initToolbar();

        initData();
        initComponent();

    }

    private void initData() {
        Intent intent = getIntent();
        roomChat = (RoomChat) intent.getExtras().getSerializable(ChatRoomFragment.ROOM_CHAT);
        position = intent.getExtras().getInt(ChatRoomFragment.POSITION);
        key = intent.getExtras().getString(ChatRoomFragment.KEY);

        Log.e(TAG, roomChat.getArea());
        arrChat.clear();
        databaseReference.child(ChatRoomFragment.ROOM_CHAT).child(key).child(ChatRoomFragment.ARR_CHAT).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ItemChat chat = dataSnapshot.getValue(ItemChat.class);
                arrChat.add(chat);
                chatAdapter.notifyDataSetChanged();
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatAdapter = new ChatAdapter(arrChat);
        recyclerView.setAdapter(chatAdapter);
        edtComment = (EditText) findViewById(R.id.edtComment);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
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
                String chat = edtComment.getText().toString();
                if (!chat.isEmpty()) {

//                    Log.e(TAG, chat);
                    String date = MyCalendar.getDay() + " / " + MyCalendar.getMonth() + " / " + MyCalendar.getYear();
                    String timeStamp = MyCalendar.getSecond() + " : " + MyCalendar.getMinute() + " : " + MyCalendar.getHour();
                    arrChat.add(new ItemChat("admin", chat, date, timeStamp));
                    chatAdapter.notifyDataSetChanged();
                    databaseReference.child(ChatRoomFragment.ROOM_CHAT).child(key).child(ChatRoomFragment.ARR_CHAT).setValue(arrChat);
                    arrChat.remove(arrChat.size() - 1);
                    edtComment.setText("");
                    recyclerView.scrollToPosition(arrChat.size() - 1);

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ChatRoomFragment.POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
