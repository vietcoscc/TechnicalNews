package com.example.vaio.technicalnews.model.application;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

/**
 * Created by vaio on 12/25/2016.
 */

public class AccountManager implements Serializable {
    private static final String TAG = "AccountManager";
    private Context context;
    private FirebaseAuth mAuth;
    private UserInfo userInfo;


    public AccountManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        });

    }


    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void login(String email, String password) {
        if (!MainActivity.isNetWorkAvailable(context)) {
            Toast.makeText(context, "No internet connection !", Toast.LENGTH_SHORT).show();
            return;
        }


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "The feilds must not empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    if (onLoginFail != null) {
                        onLoginFail.onFail();
                    }
                } else {
                    FireBaseReference.getUserIdRef(getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userInfo = dataSnapshot.getValue(UserInfo.class);
                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                            logout();
                            onLoginSuccess.onSuccess();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public void logout() {
        MySharedPreferences.clearSharedPref(context);
        mAuth.signOut();
        if (onLogout != null) {
            onLogout.logout();
        }
    }

    public void register(final String yourName, final String email, final String password) {
        if (!MainActivity.isNetWorkAvailable(context)) {
            Toast.makeText(context, "No internet connection !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty() || password.isEmpty() || yourName.isEmpty()) {
            Toast.makeText(context, "The feilds must not empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                                    setDisplayName(yourName).setPhotoUri(MainActivity.getUriToDrawable(context, R.drawable.boss)).build();
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                            if (onRegisterFail != null) {
                                                onRegisterFail.onFail();
                                            }
                                        } else {
                                            final UserInfo userInfo = new UserInfo(user.getUid(), yourName, user.getEmail(), "", MyCalendar.getDate(), false);
                                            FireBaseReference.getUserIdRef(userInfo.getUid()).setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, userInfo.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                    onRegisterSuccess.onSuccses();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } else {

                        }

                    }
                });
            }
        });
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    private OnRegisterSuccess onRegisterSuccess;
    private OnRegisterFail onRegisterFail;
    private OnLoginSuccess onLoginSuccess;
    private OnLoginFail onLoginFail;
    private OnLogout onLogout;

    public void setOnLoginSuccess(OnLoginSuccess onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void setOnLoginFail(OnLoginFail onLoginFail) {
        this.onLoginFail = onLoginFail;
    }

    public void setOnRegisterSuccess(OnRegisterSuccess onRegisterSuccess) {
        this.onRegisterSuccess = onRegisterSuccess;
    }

    public void setOnRegisterFail(OnRegisterFail onRegisterFail) {
        this.onRegisterFail = onRegisterFail;
    }

    public void setOnLogout(OnLogout onLogout) {
        this.onLogout = onLogout;
    }

    public interface OnLoginSuccess {
        void onSuccess();
    }

    public interface OnLoginFail {
        void onFail();
    }

    public interface OnRegisterSuccess {
        void onSuccses();
    }

    public interface OnRegisterFail {
        void onFail();
    }

    public interface OnLogout {
        void logout();
    }
}

