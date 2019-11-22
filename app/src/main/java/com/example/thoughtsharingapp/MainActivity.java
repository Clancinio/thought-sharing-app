package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        // sets up activity toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // TODO: Need to take care of the authenticate notification aspect when user is szigned out
        auth = FirebaseAuth.getInstance();
        databaseReferencePost = FirebaseDatabase.getInstance().getReference().child("Posts");

        //Notify user when they receive a notification
        /**listenForRequest();**/

        //Recycler View
        feedList = findViewById(R.id.feed_list);
        feedList.setHasFixedSize(true);
        feedList.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // Set the layout manager to your recyclerview
        feedList.setLayoutManager(layoutManager);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                if (menuItem.getItemId() == R.id.add_menu_navigation) {
                    Intent postActivityntent = new Intent(MainActivity.this, PostMessageActivity.class);
                    startActivity(postActivityntent);

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

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts")
                .limitToLast(10);

        FirebaseRecyclerOptions<Feed> options =
                new FirebaseRecyclerOptions.Builder<Feed>()
                        .setQuery(query, Feed.class)
                        .build();

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
                        startActivity(requestIntent);

                    }
                });
                /*********** KENNETHS MESSAGE CODE ***************/
                /* When user clicks on post layout, open the messages activity with the required information
                 * using the intent*/
                /****     holder.postLayout.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                Log.e(TAG, "User id " + model.getUserId() + " my Id " + auth.getUid());


                Intent messagesActivityIntent = new Intent(MainActivity.this, MessagesActivity.class);

                messagesActivityIntent.putExtra(POST_TEXT_EXTRAS, model.getPostText());
                messagesActivityIntent.putExtra(POST_ID_EXTRAS, model.getPostId());
                messagesActivityIntent.putExtra(USER_ID_EXTRAS, model.getUserId());
                startActivity(messagesActivityIntent);

                }
                }); ***/
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
