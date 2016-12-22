package com.example.vaio.technicalnews;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by vaio on 12/22/2016.
 */

public class ForumFragment extends Fragment {
    private Context context;

    public ForumFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override

    public View getView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.fragment_forum, null);
        return v;
    }
}
