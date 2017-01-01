package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.R;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    private AccountManager accountManager;

    public LoginFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (!MainActivity.isNetWorkAvailable(getContext())) {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnLogin.setClickable(false);
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty() || password.isEmpty()) {
                    btnLogin.setClickable(true);
                    return;
                }
                accountManager.login(userName, password);
                btnLogin.setClickable(true);
                break;
            case R.id.tvSignUp:
//                Intent intent = new Intent(LoginFragment.this, RegisterFragment.class);
//                startActivityForResult(intent, SIGN_UP_REQUEST_CODE);
                break;
        }
    }
}