package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thoughtsharingapp.classes.Feed;
import com.example.thoughtsharingapp.classes.NotificationStarter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity<StorageReference> extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Choose an arbitrary request code value
    public static final String POST_TEXT_EXTRAS = "postText";
    public static final String USER_ID_EXTRAS = "userId";
    public static final String POST_ID_EXTRAS = "postId";
    public static final String POST_TITLE_EXTRAS = "postTitle";

    // Views
    private RecyclerView feedList;
    private LinearLayoutManager layoutManager;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference databaseReferencePost;
    private DatabaseReference databaseReferenceRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SigninActivity.CheckNetwork.isInternetAvailable(MainActivity.this)) //returns true if internet available

        {
            Toast.makeText(MainActivity.this,"No Internet Connection",1000).show();
        }

        // sets up activity toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Create database instance for Posts
        databaseReferencePost = FirebaseDatabase.getInstance().getReference().child("Posts");

        //Recycler View
        feedList = findViewById(R.id.my_post_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // Set the layout manager to your recyclerView
        feedList.setLayoutManager(layoutManager);

        // Bottom navigation menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.add_menu_navigation) {
                    Intent postActivityntent = new Intent(MainActivity.this, PostActivity.class);
                    startActivity(postActivityntent);
                }
                if (menuItem.getItemId() == R.id.my_posts_navigation) {
                    Intent myPostActivity = new Intent(MainActivity.this, MyPostActivity.class);
                    startActivity(myPostActivity);
                }
                return false;
            }
        });

        NotificationStarter notification = new NotificationStarter(this);
        notification.checkForNewRequest();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Create a database query to pull data to display on the main feed
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .limitToLast(10);

        FirebaseRecyclerOptions<Feed> options =
                new FirebaseRecyclerOptions.Builder<Feed>()
                        .setQuery(query, Feed.class)
                        .build();

        // Adapter for RecyclerView
        final FirebaseRecyclerAdapter<Feed, FeedViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Feed, FeedViewHolder>(options) {

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post, parent, false);
                return new FeedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder, int position, @NonNull final Feed model) {
                holder.postText.setText(model.getPostText());
                holder.titlePost.setText(model.getPostTitle());
                //TODO: Lets do something on the layout with the post title value. Displaying it here maybe?

                final String userID = getRef(position).getKey();
                // Click post to request conversation
                holder.postLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent requestIntent = new Intent(MainActivity.this, RequestActivity.class);
                        requestIntent.putExtra(USER_ID_EXTRAS, model.getUserId());
                        requestIntent.putExtra(POST_ID_EXTRAS, model.getPostId());
                        requestIntent.putExtra(POST_TITLE_EXTRAS, model.getPostTitle());
                        requestIntent.putExtra(POST_TEXT_EXTRAS, model.getPostText());
                        startActivity(requestIntent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }


}
