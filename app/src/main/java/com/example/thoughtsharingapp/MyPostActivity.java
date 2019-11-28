package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thoughtsharingapp.classes.Feed;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyPostActivity<StorageReference> extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Choose an arbitrary request code value
    public static final String POST_TEXT_EXTRAS = "postText";
    public static final String USER_ID_EXTRAS = "userId";
    public static final String POST_ID_EXTRAS = "postId";
    public static final String POST_TITLE_EXTRAS = "postTitle";

    // Views
    private RecyclerView myPosts;
    private LinearLayoutManager layoutManager;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference databaseReferencePost;

    //FirebaseUser
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        // Database Reference
        databaseReferencePost = FirebaseDatabase.getInstance().getReference().child("Posts");

        //Recycler View
        myPosts = findViewById(R.id.my_post_list);
        myPosts.setHasFixedSize(true);
        myPosts.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        // Set the layout manager to your recyclerview
        myPosts.setLayoutManager(layoutManager);

        // Get current user
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("MyPosts").child(mCurrentUser.getUid())
                .limitToLast(100);

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
                        //******** KENNETHS CODE *********/
                        Intent messagesActivityIntent = new Intent(MyPostActivity.this, PostCommentActivity.class);
                        messagesActivityIntent.putExtra(POST_TEXT_EXTRAS, model.getPostText());
                        messagesActivityIntent.putExtra(POST_ID_EXTRAS, model.getPostId());
                        messagesActivityIntent.putExtra(USER_ID_EXTRAS, model.getUserId());
                        startActivity(messagesActivityIntent);
                    }
                });
            }
        };

        myPosts.setAdapter(firebaseRecyclerAdapter);

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



}
