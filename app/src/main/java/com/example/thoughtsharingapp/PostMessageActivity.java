package com.example.thoughtsharingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostMessageActivity extends AppCompatActivity {

    private EditText titleEditext;
    private EditText textEditext;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        titleEditext = findViewById(R.id.title_edit_text);
        textEditext = findViewById(R.id.text_edit_text);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");




    }

    public void postTitle(View view){

        if(!titleEditext.getText().toString().isEmpty() && !textEditext.getText().toString().isEmpty()){
        startPosting(titleEditext.getText().toString(),textEditext.getText().toString());
        }
        finish();

    }

    // This method posts to the database
    private void startPosting(String title,String text) {


        /**DatabaseReference newPost = databaseReference.push();
         newPost.child("post").setValue(postInput); */
        // Write a message to the database
        DatabaseReference newPost = databaseReference.push();
        newPost.child("postTitle").setValue(title);
        newPost.child("postText").setValue(text);




    }

}
