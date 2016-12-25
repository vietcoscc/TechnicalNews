package com.example.vaio.technicalnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtYourName;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnSignUp;
    private AccountManager accountManager;

//    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        accountManager = new AccountManager(this);
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Toast.makeText(RegisterActivity.this, "successful", Toast.LENGTH_SHORT).show();
//                }else {
//
//                }
//
//            }
//        }
//
//        ;

        initViews();

    }

    private void initViews() {
        edtYourName = (EditText) findViewById(R.id.yourName);
        edtUserName = (EditText) findViewById(R.id.userName);
        edtPassword = (EditText) findViewById(R.id.password);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                btnSignUp.setClickable(false);

                break;
        }
    }
}
