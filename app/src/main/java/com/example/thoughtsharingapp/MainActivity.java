package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class MainActivity<StorageReference> extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;
    public static final String POST_INFO = "PostInfo";
    public static final String POST_TEXT = "postText";
    public static final String USER_ID = "userId";
    public static final String POST_ID = "postId";

    // Views
    private RecyclerView feedList;
    private LinearLayoutManager layoutManager;
    private EditText postInput;
    private Button btnPost;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    //feed object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sets up activity toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        //Recycler View
        feedList = findViewById(R.id.feed_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // Set the layout manager to your recyclerview
        feedList.setLayoutManager(layoutManager);


        if (auth.getCurrentUser() == null) {
            // not signed in
            signIn();
        }

        btnPost = findViewById(R.id.btnPost);
        postInput = findViewById(R.id.postInput);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }

        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.add_menu_navigation) {
                    //Todo: @chuck, writes his code here
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .limitToLast(10);

        FirebaseRecyclerOptions<Feed> options =
                new FirebaseRecyclerOptions.Builder<Feed>()
                        .setQuery(query, Feed.class)
                        .build();

        FirebaseRecyclerAdapter<Feed, FeedViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Feed, FeedViewHolder>(options) {

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post, parent, false);
                FeedViewHolder viewHolder = new FeedViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder, int position, @NonNull final Feed model) {
                holder.postText.setText(model.getPostText());

                Log.e(TAG, "my id " + auth.getUid());
                Log.e(TAG, "unknown post " + model.getUserId());
                if (auth.getUid().equals(model.getUserId())) {
                    holder.titlePost.setText("Me");
                }

                /* When user clicks on post layout, open the messages activity with the required information
                 * using the intent*/
                holder.postLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "User id " + model.getUserId() + " my Id " + auth.getUid());


                        Intent messagesActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);

                        messagesActivityIntent.putExtra(POST_TEXT, model.getPostText());
                        messagesActivityIntent.putExtra(POST_ID, model.getPostId());
                        messagesActivityIntent.putExtra(USER_ID, model.getUserId());
                        startActivity(messagesActivityIntent);
                       
                    }
                });
            }
        };

        feedList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView postText;
        View postLayout;
        TextView titlePost;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            postLayout = itemView;
            titlePost = itemView.findViewById(R.id.post_title);

            postText = itemView.findViewById(R.id.post_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    // This method posts to the database
    private void startPosting() {

        final String post = postInput.getText().toString().trim();

        if (!TextUtils.isEmpty(post)) {
            /**DatabaseReference newPost = databaseReference.push();
             newPost.child("post").setValue(postInput); */
            // Write a message to the database
            DatabaseReference newPost = databaseReference.push();
            newPost.child(POST_TEXT).setValue(post);
            newPost.child(USER_ID).setValue(auth.getUid());
            newPost.child(POST_ID).setValue(newPost.getRef().getKey());


            Toast.makeText(this, "Thought posted successfully", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "user is signed in");
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e(TAG, "User pressed back button");
                    //TODO: What are we planning to do here?
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e(TAG, "user is signed inNo network");
                    //TODO: Notify user that there is no network
                    return;
                }

                //If it fails for any other unknown reason Sign in
                //signIn();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                // do your sign-out stuff
                AuthUI.getInstance().signOut(this);
                finish();
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
    }

    private void signIn() {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false, true)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build()
                                )).build()
                , RC_SIGN_IN);
    }
}
