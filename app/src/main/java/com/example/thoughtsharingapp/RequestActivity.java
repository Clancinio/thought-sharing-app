package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.thoughtsharingapp.MainActivity.USER_ID_EXTRAS;

public class RequestActivity extends AppCompatActivity {

    // Views
    private Button requestButton;
    private TextView userID;

    // State
    private int mCurrentState;

    // Database
    private FirebaseDatabase database;
    private DatabaseReference friendRequestRef;

    // Firebase Auth
    private FirebaseAuth mAuth;

    //FirebaseUser
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Receive intent from MainActivity
        final String user_id = getIntent().getStringExtra(USER_ID_EXTRAS);

        // Views
        userID = findViewById(R.id.user_id);
        userID.setText(user_id);
        requestButton = findViewById(R.id.request_button);

        // Get current user
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Store data in database
        database = FirebaseDatabase.getInstance();
        friendRequestRef = database.getReference().child("Friend_request");
        

        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendRequestRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String requestType = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(requestType.equals("received")){
                                mCurrentState = 2;
                                requestButton.setText("Accept Friend Rquest");
                            } else if(requestType.equals("sent")){
                                mCurrentState =  1; // request sent
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // OnClickListener for request button
        mCurrentState = 0;
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //------------------- NOT FRIENDS --------------------//
                // mCurrentState = 0 --- Not Friends
                // mCurrentState = 1 --- Request Sent
                // mCurrentState = 2 --- Request Received
                requestButton.setEnabled(false);
                if(mCurrentState == 0){
                    friendRequestRef.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestRef.child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        requestButton.setEnabled(true);
                                        mCurrentState = 1;
                                        requestButton.setText("Cancel Request");
                                        Toast.makeText(RequestActivity.this, "Request Sent!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(RequestActivity.this, "Request Failed!", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

                //------------------- NOT FRIENDS --------------------//
                if(mCurrentState == 1) {
                    friendRequestRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    requestButton.setEnabled(true);
                                    mCurrentState = 0;
                                    requestButton.setText("Send Request");
                                    Toast.makeText(RequestActivity.this, "Request Canceled!", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });

                }
            }
        });

    }
}
