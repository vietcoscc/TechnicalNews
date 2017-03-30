package com.example.vaio.technicalnews.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.activity.ChatActivity;
import com.example.vaio.technicalnews.activity.LoginActivity;
import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.asyntask.UploadAvatarFromRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

import static com.example.vaio.technicalnews.model.MySharedPreferences.SHARED_PREF;

/**
 * Created by vaio on 12/25/2016.
 */

public class AccountManager implements Serializable {
    private static final String TAG = "AccountManager";
    private Context context;
    private FirebaseAuth mAuth;

    private boolean signedIn = false;
    private String pathPhoto;
    private boolean isAdmin = false;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

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
            }
        });
        getPhotoPath();
    }

    public void getPhotoPath() {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            FirebaseStorage.getInstance().getReference().
                    child("Auth/" + mAuth.getCurrentUser().getUid()).
                    getDownloadUrl().
                    addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e(TAG, uri.toString());
                            pathPhoto = uri.toString();
                        }
                    });
        }
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
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
                    signedIn = true;
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseStorage.getInstance().
                            getReference().
                            child("Auth/" + mAuth.getCurrentUser().getUid()).
                            getDownloadUrl().
                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e(TAG, uri.toString());
                                    pathPhoto = uri.toString();
                                    if (onLoginSuccess != null) {
                                        onLoginSuccess.onSuccess();
                                    }
                                }
                            });


                }
            }
        });
    }

    public void logout() {
        signedIn = false;
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
                            final Uri uri = getUriToDrawable(context, R.drawable.boss);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                                    setDisplayName(yourName).setPhotoUri(uri).build();
                            FirebaseUser user = mAuth.getCurrentUser();
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
                                            UploadAvatarFromRegister uploadAvatarFromRegister = new UploadAvatarFromRegister();
                                            uploadAvatarFromRegister.setOnUploadComplete(new UploadAvatarFromRegister.OnUploadComplete() {
                                                @Override
                                                public void onComplete() {
                                                    if (onRegisterSuccess != null) {
                                                        Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                                                        onRegisterSuccess.onSuccses();
                                                    }
                                                }
                                            });
                                            uploadAvatarFromRegister.execute(uri.toString(), mAuth.getCurrentUser().getUid());
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

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }

    public static final Uri getUriToDrawable(Context context, int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isSignedIn() {
        return signedIn;
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

