package com.example.vaio.technicalnews.asyntask;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by vaio on 19/03/2017.
 */

public class UploadAvatarFromRegister extends AsyncTask<String, Void, Void> {
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private UploadTask uploadTask;

    @Override
    protected Void doInBackground(String... params) {
        String link = params[0];
        String uid = params[1];

        try {

            StorageReference storePhotoAuth = storageReference.child("Auth/" + uid);
            uploadTask = storePhotoAuth.putFile(Uri.parse(link));

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

    public void setOnUploadComplete(OnUploadComplete onUploadComplete) {
        this.onUploadComplete = onUploadComplete;
    }

    private OnUploadComplete onUploadComplete;

    public interface OnUploadComplete {
        void onComplete();
    }
}
