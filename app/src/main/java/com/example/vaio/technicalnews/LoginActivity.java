package com.example.vaio.technicalnews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private int SIGN_UP_REQUEST_CODE = 1;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountManager = new AccountManager(this);
        initViews();
    }

    private void initViews() {
        edtUserName = (EditText) findViewById(R.id.userName);
        edtPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setClickable(true);
        btnLogin.setOnClickListener(this);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvSignUp.setText(Html.fromHtml("<u>Not a member ? Sign up now</u>"));
        tvSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                btnLogin.setClickable(false);

                break;
            case R.id.tvSignUp:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, SIGN_UP_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_UP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

            }
        }
    }
}
