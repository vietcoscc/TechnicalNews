package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.adapter.ViewPagerHomeAdapter;
import com.example.vaio.technicalnews.database.MyDatabase;
import com.example.vaio.technicalnews.model.NewsItem;

import java.util.ArrayList;

/**
 * Created by vaio on 12/22/2016.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    private Context context;

    private ViewPager viewPagerHome;
    private TabLayout tabLayoutHome;
    private android.app.FragmentManager fragmentManager;
    private ViewPagerHomeAdapter viewPagerHomeAdapter;

    public HomeFragment(Context context, android.app.FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.fragment_home, container, false);
        try {
            initViews(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void initViews(final View view) throws Exception {
        viewPagerHome = (ViewPager) view.findViewById(R.id.viewPagerHome);
        tabLayoutHome = (TabLayout) view.findViewById(R.id.tabLayoutHome);
        tabLayoutHome.setupWithViewPager(viewPagerHome);
        viewPagerHomeAdapter = new ViewPagerHomeAdapter(getFragmentManager(), fragmentManager, context, tabLayoutHome.getTabCount());
        viewPagerHome.setAdapter(viewPagerHomeAdapter);
        viewPagerHome.setOffscreenPageLimit(2);
        tabLayoutHome.getTabAt(0).setIcon(R.drawable.news);
        tabLayoutHome.getTabAt(1).setIcon(R.drawable.reviews);

    }

    public ArrayList<NewsItem> getArrNewsItem() {
        return viewPagerHomeAdapter.getArrNewsItem();
    }
}
