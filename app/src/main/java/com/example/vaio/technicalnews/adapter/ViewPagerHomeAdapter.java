package com.example.vaio.technicalnews.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.vaio.technicalnews.fragment.NewsFragment;
import com.example.vaio.technicalnews.fragment.ReviewsFragment;
import com.example.vaio.technicalnews.model.NewsItem;

import java.util.ArrayList;

/**
 * Created by vaio on 2/5/2017.
 */

public class ViewPagerHomeAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private int tabCount;
    private NewsFragment newsFragment;
    private ReviewsFragment reviewsFragment;

    public ViewPagerHomeAdapter(FragmentManager fm, android.app.FragmentManager fm2, Context context, int tabCount) {
        super(fm);
        this.context = context;
        this.tabCount = tabCount;
        newsFragment = new NewsFragment();
        reviewsFragment = new ReviewsFragment(fm2);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return newsFragment;
            case 1:
                return reviewsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public ArrayList<NewsItem> getArrNewsItem() {
        return newsFragment.getArrNewsItem();
    }
}
