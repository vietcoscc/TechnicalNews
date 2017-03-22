package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.asyntask.UploadAvatarFromRegister;
import com.example.vaio.technicalnews.asyntask.UploadAvatarFromStream;
import com.example.vaio.technicalnews.model.AccountManager;
import com.example.vaio.technicalnews.model.GlobalData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String USER_NAME = "User name";
    public static final String PASSWORD = "Password";
    public static final int REQUEST_REGISTER = 1;
    public static final String SHARED_PREF = "shared preferences";
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private static final int RC_REGISTER = 2;
    private AccountManager accountManager;
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    //
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout layoutGoogleLogin;
    private LinearLayout layoutFacebookLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
        try {
            initGoogleLogin();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        layoutGoogleLogin = (LinearLayout) findViewById(R.id.googleSignIn);
        layoutFacebookLogin = (LinearLayout) findViewById(R.id.facebookSignIn);
        layoutFacebookLogin.setOnClickListener(this);
        layoutGoogleLogin.setOnClickListener(this);
    }

    private void initGoogleLogin() throws Exception {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.hide();
                        }
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() throws Exception {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loging in");
        progressDialog.setCancelable(false);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) throws Exception {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        accountManager.getmAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        setClickableViews(true);
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            if(progressDialog!=null && progressDialog.isShowing()){
                                progressDialog.hide();
                            }
                        } else {

                            UploadAvatarFromStream uploadAvatarFromStream = new UploadAvatarFromStream();
                            uploadAvatarFromStream.setOnUploadComplete(new UploadAvatarFromRegister.OnUploadComplete() {
                                @Override
                                public void onComplete() {
                                    Toast.makeText(LoginActivity.this, "Authentication successful !",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseStorage.getInstance().getReference().
                                            child("Auth/" + accountManager.getCurrentUser().getUid()).
                                            getDownloadUrl().
                                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.e(TAG, uri.toString());
                                                    accountManager.setPathPhoto(uri.toString());
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        progressDialog.hide();
                                                    }
                                                    Toast.makeText(LoginActivity.this, "Authentication successful !",
                                                            Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(progressDialog!=null && progressDialog.isShowing()){
                                                progressDialog.hide();
                                            }
                                        }
                                    });
                                }
                            });
                            uploadAvatarFromStream.execute(accountManager.getCurrentUser().getPhotoUrl().toString(),
                                    accountManager.getCurrentUser().getUid());

                        }

                    }
                });
    }

    private void setClickableViews(boolean bool) {
        btnLogin.setClickable(bool);
        tvSignUp.setClickable(bool);
        layoutFacebookLogin.setClickable(bool);
        layoutGoogleLogin.setClickable(bool);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        try {
            setClickableViews(false);
            switch (view.getId()) {
                case R.id.btnLogin:

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Signing in ... ");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    if (!MainActivity.isNetWorkAvailable(this)) {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        return;
                    }
                    if (!MainActivity.isNetWorkAvailable(this)) {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        return;
                    }

                    String userName = edtUserName.getText().toString();
                    String password = edtPassword.getText().toString();
                    if (userName.isEmpty() || password.isEmpty()) {
                        btnLogin.setClickable(true);
                        Toast.makeText(this, "The feilds must not empty", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        return;
                    }
                    accountManager.setOnLoginSuccess(new AccountManager.OnLoginSuccess() {
                        @Override
                        public void onSuccess() {
                            progressDialog.hide();
                            onBackPressed();

                        }
                    });
                    accountManager.setOnLoginFail(new AccountManager.OnLoginFail() {
                        @Override
                        public void onFail() {
                            progressDialog.hide();
                        }
                    });
                    accountManager.login(userName, password);
                    setClickableViews(true);
                    break;

                case R.id.tvSignUp:
                    setClickableViews(true);
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivityForResult(intent, RC_REGISTER);
                    overridePendingTransition(R.anim.anim_fragment_in_from_right, R.anim.anim_fragment_out_from_right);
                    break;

                case R.id.googleSignIn:

                    if (!MainActivity.isNetWorkAvailable(this)) {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    signIn();
                    break;

                case R.id.facebookSignIn:

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        try {
            if (requestCode == RC_SIGN_IN) {
                progressDialog.show();
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result != null) {
                    if (result.isSuccess()) {

                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = result.getSignInAccount();
                        firebaseAuthWithGoogle(account);
                    } else {
                        // Google Sign In failed, update UI appropriately
                        // ...
                    }
                }
            }
            if (requestCode == RC_REGISTER) {
                if (resultCode == RESULT_OK) {
                    String userName = data.getExtras().getString(USER_NAME);
                    String password = data.getExtras().getString(PASSWORD);

                    edtUserName.setText(userName);
                    edtPassword.setText(password);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fragment_in_from_left, R.anim.anim_fragment_out_from_left);
    }
}
