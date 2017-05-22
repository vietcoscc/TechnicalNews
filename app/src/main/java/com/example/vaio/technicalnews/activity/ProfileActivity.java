package com.example.vaio.technicalnews.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.application.AccountManager;
import com.example.vaio.technicalnews.model.application.FireBaseReference;
import com.example.vaio.technicalnews.model.application.GlobalData;
import com.example.vaio.technicalnews.model.forum.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.vaio.technicalnews.R.drawable.user;
import static com.example.vaio.technicalnews.model.application.FireBaseReference.MAIL;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ProfileActivity";
    private static final int RC_PICK_IMAGE = 1;
    private LinearLayout layoutEmail;
    private LinearLayout layoutDisplayName;
    private LinearLayout layoutPosted;

    private CircleImageView ivAvatar;
    private TextView tvEmail;
    private TextView tvDisplayName;
    private TextView tvAdmin;
    private TextView tvJoinedDate;
    private AccountManager accountManager;
    public static final String RC = "code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        try {
            initToolbar();
            initData();
            initViews();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() throws Exception {
        GlobalData globalData = (GlobalData) getApplication();
        accountManager = globalData.getAccountManager();
    }

    private void initViews() throws Exception {
        layoutEmail = (LinearLayout) findViewById(R.id.layoutEmail);
        layoutDisplayName = (LinearLayout) findViewById(R.id.layoutDisplayName);
        layoutPosted = (LinearLayout) findViewById(R.id.layoutPosted);
        ivAvatar = (CircleImageView) findViewById(R.id.ivAvatar);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvDisplayName = (TextView) findViewById(R.id.tvDisplayName);
        tvAdmin = (TextView) findViewById(R.id.tvAdmin);
        tvJoinedDate = (TextView) findViewById(R.id.tvJoinedDate);

        layoutEmail.setOnClickListener(this);
        layoutDisplayName.setOnClickListener(this);
        layoutPosted.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
    }

    private void updateUI() throws Exception {
        if (accountManager.getCurrentUser() == null) {
            return;
        }
        FireBaseReference.getAccountRef().child(accountManager.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                ivAvatar.setVisibility(View.VISIBLE);
                String uri = userInfo.getPhotoUrl();
                if (uri != null && !uri.isEmpty()) {
                    Picasso.with(ProfileActivity.this).load(uri).error(R.drawable.warning).placeholder(R.drawable.loading).into(ivAvatar);
                }

                String displayName = userInfo.getDisplayName();
                String email = userInfo.getEmail();
                tvDisplayName.setText(displayName);
                tvEmail.setText(email);
                tvJoinedDate.setText(userInfo.getJoinedDate());
                tvAdmin.setText(userInfo.isAdmin() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.layoutEmail:

                    break;
                case R.id.layoutDisplayName:
                    actionOnLayoutDisplayNameClick();
                    break;
                case R.id.layoutPosted:
                    Intent intent = new Intent(ProfileActivity.this, PostedActivity.class);
                    intent.putExtra(MAIL, accountManager.getCurrentUser().getEmail());
                    startActivity(intent);
                    break;
                case R.id.ivAvatar:
                    actionIvAvatarClick();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void actionOnLayoutDisplayNameClick() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        final EditText edtDisplayName = new EditText(this);
        edtDisplayName.setHint("Display name ... ");

        builder.setMessage(accountManager.getCurrentUser().getDisplayName());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart((int) getResources().getDimension(R.dimen.activity_horizontal_margin));
        params.setMarginEnd((int) getResources().getDimension(R.dimen.activity_horizontal_margin));
        edtDisplayName.setLayoutParams(params);
        builder.setView(edtDisplayName);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                        setDisplayName(edtDisplayName.getText().toString()).build();
                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
                accountManager.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this, "Successful !", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        accountManager.logout();
                        onBackPressed();
                    }
                });
            }
        });
        builder.create().show();
    }

    private void actionIvAvatarClick() throws Exception {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_PICK_IMAGE);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == RC_PICK_IMAGE) {
                final Uri uri = data.getData();
//                Toast.makeText(this, uri + "", Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, uri.getPath() + "", Toast.LENGTH_SHORT).show();

//                Toast.makeText(this, b.getWidth() + "", Toast.LENGTH_SHORT).show();
//                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//
//                Log.e(TAG, uri.toString());
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
//                        setPhotoUri(uri).build();
//                accountManager.getCurrentUser().updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(ProfileActivity.this, "Successful !", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                        accountManager.logout();
//                        onBackPressed();
//                    }
//                });
//                StorageReference storePhotoAuth = FirebaseStorage.getInstance().getReference().child("Auth/" + accountManager.getCurrentUser().getUid());
//                UploadTask uploadTask = storePhotoAuth.putBytes(byteArray);
//                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        FirebaseStorage.getInstance().getReference().child("Auth/" + accountManager.getCurrentUser().getUid()).
//                                getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//
//                            }
//                        });
//
//                    }
//                });
//

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
