package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thoughtsharingapp.classes.Feed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostMessageActivity extends AppCompatActivity {
    private static final String TAG = PostMessageActivity.class.getSimpleName();
    private EditText titleEditext;
    private EditText textEditext;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        titleEditext = findViewById(R.id.title_edit_text);
        textEditext = findViewById(R.id.text_edit_text);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        auth = FirebaseAuth.getInstance();
    }

    public void postTitle(View view) {

        if (!titleEditext.getText().toString().isEmpty() && !textEditext.getText().toString().isEmpty()) {
            startPosting(titleEditext.getText().toString(), textEditext.getText().toString());

        }else{
            Toast.makeText(this, "Thinking of posting something right?", Toast.LENGTH_SHORT).show();
        }


    }

    // This method posts to the database
    private void startPosting(String title, String text) {


        DatabaseReference newPost = databaseReference.push();
        newPost.setValue(new Feed(title, text, auth.getUid(), newPost.getRef().getKey())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Posted successfully");
                    Toast.makeText(PostMessageActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //TODO: come up with a better design to notify them that they couldnt post
                    Log.e(TAG, "Posting failed");
                    Toast.makeText(PostMessageActivity.this, "Posting failed", Toast.LENGTH_SHORT).show();


                }
            }
        });


    }
}