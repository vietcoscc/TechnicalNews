package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.LoginFragment;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.GlobalData;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AccountManager.OnLoginSuccess {
    public static final String USER_NAME = "User name";
    public static final String PASSWORD = "Password";
    public static final int REQUEST_REGISTER = 1;
    public static final String SHARED_PREF = "shared preferences";
    private AccountManager accountManager;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        accountManager.setOnLoginSuccess(this);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loging in ... ");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fragment_in_from_left, R.anim.anim_fragment_out_from_left);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                progressDialog.show();
                if (!MainActivity.isNetWorkAvailable(this)) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    return;
                }
                btnLogin.setClickable(false);
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty() || password.isEmpty()) {
                    btnLogin.setClickable(true);
                    Toast.makeText(this, "The feilds must not empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                accountManager.login(userName, password);
                btnLogin.setClickable(true);
                break;
            case R.id.tvSignUp:

                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
                overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
            edtUserName.setText(data.getExtras().getString(USER_NAME));
            edtPassword.setText(data.getExtras().getString(PASSWORD));
        }
    }
}

    @Override
    public void onSuccess() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, edtUserName.getText().toString());
        editor.putString(PASSWORD, edtPassword.getText().toString());
        editor.commit();

        progressDialog.hide();
        onBackPressed();
    }
}
