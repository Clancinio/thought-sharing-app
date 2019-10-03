package com.example.thoughtsharingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> thoughts;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        thoughts = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        thoughts.add("To be Changed");
        ThoughtInfoAdapter adapter = new ThoughtInfoAdapter(this, thoughts);
        listView.setAdapter(adapter);
    }
}
