package com.example.vaio.technicalnews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by vaio on 30/03/2017.
 */

public class BannedFragment extends Fragment {
    private static final String TAG = "BannedFragment";
    private AccountManager accountManager;
    private ArrayList<String> arrBanned = new ArrayList<>();
    private ArrayList<String> arrKey = new ArrayList<>();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private TextView tvEmpty;

    public BannedFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_banned, container, false);

        initViews(view);
        receiveData();
        return view;
    }

    private void initViews(View view) {
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrBanned);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_unban, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        FireBaseReference.getAccountRef().child(arrKey.get(position)).child(FireBaseReference.BAN).setValue(false);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void receiveData() {

        arrBanned.clear();
        arrKey.clear();
        FireBaseReference.getAccountRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserInfo value = dataSnapshot.getValue(UserInfo.class);
                if (value.isBanned()) {
                    arrBanned.add(value.getEmail() + "\n" + value.getDisplayName());
                    arrKey.add(dataSnapshot.getKey());
                    adapter.notifyDataSetChanged();
                }
                if (arrBanned.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    UserInfo value = dataSnapshot.getValue(UserInfo.class);
                    Log.e(TAG, arrBanned.indexOf(value.getEmail() + "\n" + value.getDisplayName()) + "");
                    if (arrBanned.indexOf(value.getEmail() + "\n" + value.getDisplayName()) >= arrBanned.size()) {
                        return;
                    }
                    arrBanned.remove(arrBanned.indexOf(value.getEmail() + "\n" + value.getDisplayName()));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
