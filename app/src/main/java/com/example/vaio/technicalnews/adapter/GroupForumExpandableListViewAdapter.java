package com.example.vaio.technicalnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.ChildForumItem;
import com.example.vaio.technicalnews.model.GroupForumItem;

import java.util.ArrayList;

/**
 * Created by vaio on 21/03/2017.
 */

public class GroupForumExpandableListViewAdapter extends BaseExpandableListAdapter {
    private ArrayList<GroupForumItem> arrGroupForumItem;
    private AnimationSet animationSetExpand;
    private AnimationSet animationSetCollapse;
    private Context context;
    private AccountManager accountManager;
    public GroupForumExpandableListViewAdapter(Context context, ArrayList<GroupForumItem> arrGroupForumItem, AccountManager accountManager) {
        this.arrGroupForumItem = arrGroupForumItem;
        this.context = context;
        this.accountManager = accountManager;
        animationSetCollapse = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_collapse_list);
        animationSetExpand = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_expand_list);
    }

    @Override
    public int getGroupCount() {
        return arrGroupForumItem.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return arrGroupForumItem.get(groupPosition).getArrChildForumItem().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return arrGroupForumItem.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return arrGroupForumItem.get(groupPosition).getArrChildForumItem().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_group_forum_expandable_list_view, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(arrGroupForumItem.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_child_forum_expandable_list_view, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvTopicNumber = (TextView) convertView.findViewById(R.id.tvTopicNumber);
        TextView tvPostNumber = (TextView) convertView.findViewById(R.id.tvPostNumer);
        ChildForumItem childForumItem = arrGroupForumItem.get(groupPosition).getArrChildForumItem().get(childPosition);
        tvName.setText(childForumItem.getName());
        tvTopicNumber.setText(childForumItem.getPostNumber() + "");
        tvPostNumber.setText(childForumItem.getPostNumber() + "");
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
