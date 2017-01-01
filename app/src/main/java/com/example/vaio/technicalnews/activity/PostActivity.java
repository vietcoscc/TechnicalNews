package com.example.vaio.technicalnews.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;

/**
 * Created by vaio on 12/28/2016.
 */

public class PostActivity extends AppCompatActivity {
    private ImageView ivAvatar;
    private TextView tvName;
    private Spinner spinnerType;
    private EditText edtContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initViews();
    }

    private void initViews() {
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvName = (TextView) findViewById(R.id.tvName);
        spinnerType = (Spinner) findViewById(R.id.spinerType);
        edtContent = (EditText) findViewById(R.id.edtContent);
        String[] type = {MainActivity.TYPE_1, MainActivity.TYPE_1, MainActivity.TYPE_1, MainActivity.TYPE_1};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, type);
        spinnerType.setAdapter(adapter);
    }
}
