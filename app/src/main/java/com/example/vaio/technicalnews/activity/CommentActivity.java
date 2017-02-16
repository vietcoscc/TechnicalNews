package com.example.vaio.technicalnews.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vaio.technicalnews.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initOthers();
        initViews();
    }

    private void initOthers() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    private void initViews() {

    }
}
