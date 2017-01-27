package com.example.vaio.technicalnews.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by vaio on 12/28/2016.
 */

public class PostActivity extends AppCompatActivity {
    private ImageView ivAvatar;
    private TextView tvName;
    private Spinner spinnerType;
    private EditText edtContent;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initViews();


    }

    private void initViews() {
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(getIntent().getExtras().getString(MainActivity.DISPLAY_NAME));
        spinnerType = (Spinner) findViewById(R.id.spinerType);
        edtContent = (EditText) findViewById(R.id.edtContent);
        String[] type = {MainActivity.TYPE_1, MainActivity.TYPE_2, MainActivity.TYPE_3};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, type);
        spinnerType.setAdapter(adapter);
        fab = (FloatingActionButton) findViewById(R.id.fabDone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = spinnerType.getSelectedItem().toString();
                String content = edtContent.getText().toString();
                Calendar calendar = Calendar.getInstance();

                String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + "";
                String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "";
                String email = getIntent().getExtras().getString(MainActivity.EMAIL);
                String name = getIntent().getExtras().getString(MainActivity.DISPLAY_NAME);

                Topic topic = new Topic(content, date, time, 0, 0, 0, email, name);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference reference = firebaseDatabase.getReference();
                reference.child(MainActivity.TOPIC).child(type).push().setValue(topic);
                finish();
            }
        });
    }
}
