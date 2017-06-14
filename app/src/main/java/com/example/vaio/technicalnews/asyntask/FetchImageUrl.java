package com.example.vaio.technicalnews.asyntask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
            URL url1 = new URL(url);
            URLConnection connection = url1.openConnection();
            InputStream stream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            drawable = new BitmapDrawable(bitmap);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int x = windowManager.getDefaultDisplay().getWidth();
            drawable.setBounds(0, 0, x, x * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
