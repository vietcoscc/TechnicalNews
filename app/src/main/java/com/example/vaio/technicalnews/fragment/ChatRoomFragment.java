package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.ChatActivity;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.adapter.RoomChatAdapter;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.GlobalData;
import com.example.vaio.technicalnews.model.ItemChat;
import com.example.vaio.technicalnews.model.RoomChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vaio on 15/03/2017.
 */

public class ChatRoomFragment extends Fragment {
    public static final String TAG = "ChatRoomFragment";
    public static final String AREA = "area";
    public static final String ROOM_CHAT = "Room chat";
    public static final String ARR_CHAT = "arrChat";
    public static final String ONLINE_NUMBER = "onlineNumber";
    public static final String NAME = "name";
    public static final String CHAT = "chat";
    public static final int REQUEST_CODE = 4;
    public static final String POSITION = "position";
    public static final String KEY = "key";


    private RecyclerView recyclerView;
    private String[] area = {"Hà Nội", "Hải Phòng", "TP Hồ Chí Minh"};
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<RoomChat> arrRoomChat = new ArrayList<>();
    private ArrayList<String> arrKeyRoomChat = new ArrayList<>();
    private RoomChatAdapter adapter;
    private AccountManager accountManager;

    public ChatRoomFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        accountManager.log();
        getData();
        initViews(view);
        return view;
    }

    private void getData() {
        arrRoomChat.clear();
        adapter = new RoomChatAdapter(arrRoomChat);
        databaseReference.child(ROOM_CHAT).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RoomChat roomChat = dataSnapshot.getValue(RoomChat.class);
                arrRoomChat.add(roomChat);
                String key = dataSnapshot.getKey();
                arrKeyRoomChat.add(key);
                Log.e(TAG, arrRoomChat.size() + "");
                adapter.notifyDataSetChanged();
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

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        Log.e(TAG, arrRoomChat.size() + "");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new RoomChatAdapter.OnItemClick() {
            @Override
            public void onClick(View view, int position) {
                if (!MainActivity.isNetWorkAvailable(getContext())) {
                    Toast.makeText(getContext(), "No internet connection !", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(ROOM_CHAT, arrRoomChat.get(position));
                intent.putExtra(POSITION, position);
                intent.putExtra(KEY, arrKeyRoomChat.get(position));

                RoomChat roomChat = arrRoomChat.get(position);
                roomChat.setOnlineNumber(roomChat.getOnlineNumber() + 1);

                databaseReference.child(ROOM_CHAT).child(arrKeyRoomChat.get(position)).setValue(roomChat);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getExtras().getInt(POSITION);
                RoomChat roomChat = arrRoomChat.get(position);
                if (roomChat.getOnlineNumber() > 0) {
                    roomChat.setOnlineNumber(roomChat.getOnlineNumber() - 1);
                } else {
                    roomChat.setOnlineNumber(0);
                }
                databaseReference.child(ROOM_CHAT).child(arrKeyRoomChat.get(position)).setValue(roomChat);
            }
        }
    }
}
