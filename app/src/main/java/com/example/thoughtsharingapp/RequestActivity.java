package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thoughtsharingapp.classes.FriendRequest;
import com.example.thoughtsharingapp.classes.NotificationStarter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.thoughtsharingapp.MainActivity.POST_ID_EXTRAS;
import static com.example.thoughtsharingapp.MainActivity.USER_ID_EXTRAS;

public class RequestActivity extends AppCompatActivity {
    //Constants
    public static final String CANCEL_REQUEST = "Cancel Request";
    public static final String FRIEND_REQEST_REFERENCE = "Friend_Reqest";

    public static final String REQEST_RECEIVED_REFERENCE = "request_received";
    public static final String REQEST_SENT_REFERENCE = "request_sent";
    public static final String REQEST_ACCEPTED_REFERENCE = "request_accepted";


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

    /**
     * Stores the id of the person who made the post
     **/
    private String postUserId; // The id of the person who made that post
    private String postId; // Id of the post
    //FirebaseUser
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);


        // Recieve intent from MainActivity
        postUserId = getIntent().getStringExtra(USER_ID_EXTRAS);
        postId = getIntent().getStringExtra(POST_ID_EXTRAS);

        // Views
        userID = findViewById(R.id.user_id);
        userID.setText(postUserId);
        requestButton = findViewById(R.id.request_button);


        // Get current user
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        //Store data in database
        database = FirebaseDatabase.getInstance();
        friendRequestRef = database.getReference().child(FRIEND_REQEST_REFERENCE);


        hasAlreadyRequested();
        NotificationStarter notification = new NotificationStarter(this);
        notification.checkForNewRequest();


    }


    private void cancelRequest() {
        friendRequestRef.child(mCurrentUser.getUid()).child(REQEST_SENT_REFERENCE).child(postId).removeValue();
        friendRequestRef.child(postUserId).child(REQEST_RECEIVED_REFERENCE).child(postId).child(mCurrentUser.getUid()).removeValue();
    }

    /**
     * @return returns true if you have already made a request to this post
     */
    private void hasAlreadyRequested() {
        friendRequestRef.child(mCurrentUser.getUid()).child(REQEST_SENT_REFERENCE).child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requestButton.setText(CANCEL_REQUEST);
                }
                requestButton.setEnabled(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method makes a request when user has not yet made a request
     *
     * @param view
     */
    public void completeRequest(View view) {
        if (((Button) view).getText().toString().equals(CANCEL_REQUEST)) {

            cancelRequest();
            requestButton.setText("Send Request");

        } else {
            //You can now make a request if you have not yet made a request
            if (!postUserId.equals(mCurrentUser.getUid())) { //This is true when someone else posted these thoughts but not you!!!

                makeRequest();

                requestButton.setText(CANCEL_REQUEST);

            } else {
                Toast.makeText(RequestActivity.this, "You can not make a request to yourself you know :)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * The purpose of this method is to check if the user has already made a request to this current post
     *
     * @return true if this user has already made a post
     */
    private void makeRequest() {
        //This code is to make sure you are not making a request to the same post
        friendRequestRef.child(mCurrentUser.getUid()).child(REQEST_SENT_REFERENCE).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //Push the id of the post to the person who actually made the post for them to receive a request
                    friendRequestRef.child(postUserId).child(REQEST_RECEIVED_REFERENCE).child(postId).child(mCurrentUser.getUid()).setValue("They received the request");


                    //Push success code request to yourself to avoid you making the same request to the same post
                    friendRequestRef.child(mCurrentUser.getUid()).child(REQEST_SENT_REFERENCE).child(postId).setValue("I sent the request");


                } else {
                    Toast.makeText(RequestActivity.this, "Already made a request to this person... keep waiting!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO: Notify them there is an issue with the network
            }
        });
    }


}
