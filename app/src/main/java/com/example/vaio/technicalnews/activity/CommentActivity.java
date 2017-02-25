package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView tvObject;
    private TextView tvContent;
    private Toolbar toolbar;

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
        Intent intent = getIntent();
        tvObject = (TextView) findViewById(R.id.tvObject);
        tvContent = (TextView) findViewById(R.id.tvContent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
