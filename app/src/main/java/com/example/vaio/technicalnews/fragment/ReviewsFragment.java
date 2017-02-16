package com.example.vaio.technicalnews.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.database.MyDatabase;

/**
 * Created by vaio on 2/5/2017.
 */

public class ReviewsFragment extends Fragment {
    private Context context;

    public ReviewsFragment(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        return view;
    }
}
