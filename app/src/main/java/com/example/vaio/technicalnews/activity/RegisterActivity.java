package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.GlobalData;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AccountManager.OnRegisterSuccess, AccountManager.OnRegisterFail {
    private EditText edtYourName;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnSignUp;

    private AccountManager accountManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        accountManager.setOnRegisterSuccess(this);
        accountManager.setOnRegisterFail(this);
        initViews();
    }

    private void initViews() {
        edtYourName = (EditText) findViewById(R.id.yourName);
        edtUserName = (EditText) findViewById(R.id.userName);
        edtPassword = (EditText) findViewById(R.id.password);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up ... ");
        progressDialog.setCancelable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                btnSignUp.setClickable(false);

                progressDialog.show();

                if (!MainActivity.isNetWorkAvailable(this)) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                String yourName = edtYourName.getText().toString();
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty() || password.isEmpty() || yourName.isEmpty()) {
                    btnSignUp.setClickable(true);
                    Toast.makeText(this, "The feilds must not empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                accountManager.register(yourName, userName, password);
                btnSignUp.setClickable(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fragment_in_from_left, R.anim.anim_fragment_out_from_left);
    }

    @Override
    public void onSuccses() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.USER_NAME, edtUserName.getText().toString());
        intent.putExtra(LoginActivity.PASSWORD, edtPassword.getText().toString());
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void onFail() {

    }
}
