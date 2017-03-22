package com.example.vaio.technicalnews.asyntask;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vaio on 19/03/2017.
 */

public class UploadAvatarFromStream extends AsyncTask<String, Void, Void> {
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private UploadTask uploadTask;

    @Override
    protected Void doInBackground(String... params) {
        String link = params[0];
        String uid = params[1];

        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            StorageReference storePhotoAuth = storageReference.child("Auth/" + uid);
            uploadTask = storePhotoAuth.putStream(inputStream);

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
        });
    }

    public void setOnUploadComplete(UploadAvatarFromRegister.OnUploadComplete onUploadComplete) {
        this.onUploadComplete = onUploadComplete;
    }

    private UploadAvatarFromRegister.OnUploadComplete onUploadComplete;

    public interface OnUploadComplete {
        void onComplete();
    }
}
