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
    private int tabCount;
    private NewsFragment newsFragment;
    private ReviewsFragment reviewsFragment;

    public ViewPagerHomeAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        newsFragment = new NewsFragment();
        reviewsFragment = new ReviewsFragment();
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (onTabClick != null) {
                    onTabClick.onClick(position);
                }
                return newsFragment;
            case 1:
                if (onTabClick != null) {
                    onTabClick.onClick(position);
                }
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

    public void setOnTabClick(OnTabClick onTabClick) {
        this.onTabClick = onTabClick;
    }

    private OnTabClick onTabClick;

    public interface OnTabClick {
        void onClick(int position);
    }
}
