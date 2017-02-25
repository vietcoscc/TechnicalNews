package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;

public class LoginFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private AccountManager accountManager;
    private Handler handlerSignUp;

    public LoginFragment(AccountManager accountManager, Handler handlerSignUp) {
        this.accountManager = accountManager;
        this.handlerSignUp = handlerSignUp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        edtUserName = (EditText) view.findViewById(R.id.userName);
        edtPassword = (EditText) view.findViewById(R.id.password);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnLogin.setClickable(true);
        btnLogin.setOnClickListener(this);
        tvSignUp = (TextView) view.findViewById(R.id.tvSignUp);
        tvSignUp.setText(Html.fromHtml("<u>Not a member ? Sign up now</u>"));
        tvSignUp.setOnClickListener(this);
        edtUserName.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                edtPassword.setInputType(0);
                edtUserName.setInputType(0);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtUserName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);

                if (!MainActivity.isNetWorkAvailable(getContext())) {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnLogin.setClickable(false);
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty() || password.isEmpty()) {
                    btnLogin.setClickable(true);
                    Toast.makeText(getContext(), "The feilds must not empty", Toast.LENGTH_SHORT).show();
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtUserName.setInputType(InputType.TYPE_CLASS_TEXT);
                    return;
                }
                accountManager.login(userName, password);
                btnLogin.setClickable(true);
                break;
            case R.id.tvSignUp:
                Message message = new Message();
                message.what = MainActivity.WHAT_SIGN_UP;
                handlerSignUp.sendMessage(message);
                break;
        }
    }
}
