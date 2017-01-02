package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.MyAdapter;
import com.example.vaio.technicalnews.model.Topic;

import java.util.ArrayList;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    private Context context;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Topic> arrTopic;

    public ForumFragment(Context context, ArrayList<Topic> arrTopic) {
        this.arrTopic = arrTopic;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_forum, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdapter(arrTopic);
        adapter.setClickListener(new MyAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(context, position+"", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
