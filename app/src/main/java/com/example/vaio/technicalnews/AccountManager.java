package com.example.vaio.technicalnews;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.Executor;

/**
 * Created by vaio on 12/25/2016.
 */

public class AccountManager {
    private FirebaseAuth mAuth;
    private Context context;

    public AccountManager(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
                }
            }
        });
    }

    public void register(String yourName, String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (yourName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            progressDialog.dismiss();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();

                }
            }
        });
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(yourName).build();

        mAuth.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void logout() {
        mAuth.signOut();
    }
}
