package com.example.vaio.technicalnews.fragment;

import android.app.Fragment;
import android.os.Build;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vaio.technicalnews.activity.MainActivity;
import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.AccountManager;

public class RegisterFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private EditText edtYourName;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnSignUp;

    private AccountManager accountManager;

    public RegisterFragment(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        edtYourName = (EditText) view.findViewById(R.id.yourName);
        edtUserName = (EditText) view.findViewById(R.id.userName);
        edtPassword = (EditText) view.findViewById(R.id.password);
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        edtYourName.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                if (!MainActivity.isNetWorkAvailable(getContext())) {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                btnSignUp.setClickable(false);
                String yourName = edtYourName.getText().toString();
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty() || password.isEmpty() || yourName.isEmpty()) {
                    btnSignUp.setClickable(true);
                    Toast.makeText(getContext(), "The feilds must not empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                accountManager.register(yourName, userName, password);
                btnSignUp.setClickable(true);
                break;
        }
    }


}
