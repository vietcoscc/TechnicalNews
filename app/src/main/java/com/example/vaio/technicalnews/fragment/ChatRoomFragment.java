package com.example.vaio.technicalnews.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.ChatActivity;
import com.example.vaio.technicalnews.activity.LoginActivity;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.adapter.chat.RoomChatAdapter;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.chat.ChatRoom;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vaio on 15/03/2017.
 */

public class ChatRoomFragment extends Fragment {
    public static final String TAG = "ChatRoomFragment";

    public static final int REQUEST_CODE = 4;
    public static final String POSITION = "position";
    public static final String KEY = "key";


    private RecyclerView recyclerView;
    private ArrayList<ChatRoom> arrRoomChat = new ArrayList<>();
    private RoomChatAdapter adapter;
    private AccountManager accountManager;
    private ContentLoadingProgressBar contentLoadingProgressBar;

    public ChatRoomFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        getChatRoom();
        initViews(view);
        this.view = view;
        Log.e(TAG, "");
        return view;
    }

    private void getChatRoom() {
        arrRoomChat.clear();
        adapter = new RoomChatAdapter(arrRoomChat, accountManager);
//        FireBaseReference.getRoomChatRef().keepSynced(true);
        FireBaseReference.getChatRoomRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                chatRoom.setKey(dataSnapshot.getKey());
                arrRoomChat.add(chatRoom);
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
//        FireBaseReference.getRoomChatRef().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                contentLoadingProgressBar.hide();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    private void initViews(View view) {
        contentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.contentLoadingProgressBar);
        contentLoadingProgressBar.show();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        Log.e(TAG, arrRoomChat.size() + "");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new RoomChatAdapter.OnItemClick() {
            @Override
            public void onClick(View view, int position) {
                if (accountManager.getCurrentUser() == null) {
                    Snackbar.make(view, "Sign in now ?", Snackbar.LENGTH_LONG).setAction("SIGN IN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showActivityLogin();
                        }
                    }).show();
                    return;
                }
                if (!MainActivity.isNetWorkAvailable(getContext())) {
                    Toast.makeText(getContext(), "No internet connection !", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(FireBaseReference.CHAT_ROOM, arrRoomChat.get(position));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void showActivityLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        MainActivity mainActivity = (MainActivity) getContext();
        mainActivity.overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//
            }
        }
    }
}
