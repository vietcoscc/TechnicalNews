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
import com.google.firebase.auth.UserInfo;
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
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    signedIn = true;
                } else {
                    signedIn = false;
                }
                Log.e(TAG, signedIn + "");
            }
        });
    }

    public void login(String email, String password) {
        if (!MainActivity.isNetWorkAvailable(context)) {
            Toast.makeText(context, "No internet connection !", Toast.LENGTH_SHORT).show();
            return;
        }
        logout();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "The feilds must not empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    signedIn = false;
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    if (onLoginFail != null) {
                        onLoginFail.onFail();
                    }
                } else {
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    if (onLoginSuccess != null) {
                        onLoginSuccess.onSuccess();
                    }
                }
            }
        });
    }

    public void logout() {
        mAuth.signOut();
    }

    public void register(final String yourName, final String email, final String password) {
        if (!MainActivity.isNetWorkAvailable(context)) {
            Toast.makeText(context, "No internet connection !", Toast.LENGTH_SHORT).show();
            return;
        }


        if (email.isEmpty() || password.isEmpty()) {

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
                                    if (onRegisterFail != null) {
                                        onRegisterFail.onFail();
                                    }
                                } else {
                                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                                    if (onRegisterSuccess != null) {
                                        onRegisterSuccess.onSuccses();
                                    }
                                }

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

    public void setOnLoginFail(OnLoginFail onLoginFail) {
        this.onLoginFail = onLoginFail;
    }

    private OnLoginFail onLoginFail;

    public interface OnLoginFail {
        void onFail();
    }

    public void setOnRegisterSuccess(OnRegisterSuccess onRegisterSuccess) {
        this.onRegisterSuccess = onRegisterSuccess;
    }

    private OnRegisterSuccess onRegisterSuccess;

    public interface OnRegisterSuccess {
        void onSuccses();
    }

    public void setOnRegisterFail(OnRegisterFail onRegisterFail) {
        this.onRegisterFail = onRegisterFail;
    }

    private OnRegisterFail onRegisterFail;

    public interface OnRegisterFail {
        void onFail();
    }
}
