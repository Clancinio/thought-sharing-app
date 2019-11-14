package com.example.thoughtsharingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import static com.example.thoughtsharingapp.MainActivity.USER_ID_EXTRAS;

public class RequestActivity extends AppCompatActivity {

    private Button requestButton;
    private TextView userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        String user_id = getIntent().getStringExtra(USER_ID_EXTRAS);
        userID = findViewById(R.id.user_id);
        userID.setText(user_id);
        requestButton = findViewById(R.id.request_button);

    }
}
