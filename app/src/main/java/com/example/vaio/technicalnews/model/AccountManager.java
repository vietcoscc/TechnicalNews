package com.example.vaio.technicalnews.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 12/25/2016.
 */

public class AccountManager implements Serializable {
    private static final String TAG = "AccountManager";
    private Context context;
    private FirebaseAuth mAuth;
    private boolean signedIn = false;

    public AccountManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void login(String email, String password) {
        logout();
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in ... ");
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
                    signedIn = false;
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    if (onLoginSuccess != null) {
                        onLoginSuccess.onSuccess();
                        signedIn = true;
                    }
                }
            }
        });
    }

    public void logout() {
        signedIn = false;
        mAuth.signOut();
    }

    public void register(final String yourName, final String email, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Signing up ... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (email.isEmpty() || password.isEmpty()) {
            progressDialog.dismiss();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(yourName).build();
                        auth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                                    if (onRegisterSuccess != null) {
                                        onRegisterSuccess.onSuccses();
                                    }
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                });


            }
        });


    }


    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    public void setOnLoginSuccess(OnLoginSuccess onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    private OnLoginSuccess onLoginSuccess;

    public interface OnLoginSuccess {
        void onSuccess();
    }

    public void setOnRegisterSuccess(OnRegisterSuccess onRegisterSuccess) {
        this.onRegisterSuccess = onRegisterSuccess;
    }

    private OnRegisterSuccess onRegisterSuccess;

    public interface OnRegisterSuccess {
        void onSuccses();
    }
}
