package com.example.vaio.technicalnews.asyntask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by Nguyễn Quốc Việt on 30/05/2017.
 */

public class FetchImageUrl extends AsyncTask<String, Void, Boolean> {
    private Context context;
    private Drawable drawable;

    public FetchImageUrl(Context context) {
        this.context = context;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = params[0];
        try {
            Bitmap bitmap = Picasso.with(context).load(url).get();
            drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
