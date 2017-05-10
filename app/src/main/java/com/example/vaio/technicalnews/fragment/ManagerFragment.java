package com.example.vaio.technicalnews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.ManagerActivity;
import com.example.vaio.technicalnews.model.application.AccountManager;

/**
 * Created by vaio on 30/03/2017.
 */

public class ManagerFragment extends Fragment implements View.OnClickListener {
    private TextView tvBanned;
    private TextView tvDeleted;
    private AccountManager accountManager;
    private ManagerActivity managerActivity;

    public ManagerFragment(AccountManager accountManager, ManagerActivity managerActivity) {
        this.accountManager = accountManager;
        this.managerActivity = managerActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_manager, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        tvBanned = (TextView) view.findViewById(R.id.tvBanned);
        tvDeleted = (TextView) view.findViewById(R.id.tvDeleted);
        tvBanned.setOnClickListener(this);
        tvDeleted.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBanned:
                managerActivity.loadContentMain(ManagerActivity.BANNED_TAG);
                break;
            case R.id.tvDeleted:
                managerActivity.loadContentMain(ManagerActivity.DELETED_TAG);
                break;
        }
    }
}
