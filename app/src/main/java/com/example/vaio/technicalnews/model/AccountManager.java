package com.example.vaio.technicalnews.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 12/25/2016.
 */

public class AccountManager implements Serializable {
    private Context context;
    private FirebaseAuth mAuth;
    private Handler handler;

    public AccountManager(Context context, Handler handler) {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.handler = handler;
    }


    public void login(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (email.isEmpty() || password.isEmpty()) {
            progressDialog.dismiss();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = MainActivity.WHAT_SIGN_IN_SIGN_UP;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void logout() {
        mAuth.signOut();
    }

    public void register(final String yourName, String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (email.isEmpty() || password.isEmpty()) {
            progressDialog.dismiss();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(yourName).build();

                mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = MainActivity.WHAT_SIGN_IN_SIGN_UP;
                    message.arg1 = 1;
                    handler.sendMessage(message);
                }
                progressDialog.dismiss();
            }
        });


    }


    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
