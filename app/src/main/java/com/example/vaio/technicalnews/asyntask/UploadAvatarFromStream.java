package com.example.vaio.technicalnews.asyntask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vaio on 19/03/2017.
 */

public class UploadAvatarFromStream extends AsyncTask<String, Void, Void> {
    private static final String TAG = "UploadAvatarFromStream";
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private UploadTask uploadTask;
    private Context contenxt;

    public UploadAvatarFromStream(Context contenxt) {
        this.contenxt = contenxt;
    }

    @Override
    protected Void doInBackground(String... params) {
        String link = params[0];
        String uid = params[1];
        Log.e(TAG, link);
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            StorageReference storePhotoAuth = storageReference.child("Auth/" + uid);
            uploadTask = storePhotoAuth.putStream(inputStream);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (onUploadComplete != null) {
                    onUploadComplete.onComplete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (onUploadFailure != null) {
                    onUploadFailure.onFailure();
                }
            }
        });
    }

    public void setOnUploadComplete(UploadAvatarFromRegister.OnUploadComplete onUploadComplete) {
        this.onUploadComplete = onUploadComplete;
    }

    public void setOnUploadFailure(OnUploadFailure onUploadFailure) {
        this.onUploadFailure = onUploadFailure;
    }

    private OnUploadFailure onUploadFailure;
    private UploadAvatarFromRegister.OnUploadComplete onUploadComplete;

    public interface OnUploadComplete {
        void onComplete();
    }

    public interface OnUploadFailure {
        void onFailure();
    }
}
