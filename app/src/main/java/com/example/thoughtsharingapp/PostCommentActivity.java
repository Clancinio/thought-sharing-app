package com.example.thoughtsharingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thoughtsharingapp.classes.Feed;
import com.example.thoughtsharingapp.classes.Thoughts;
import com.example.thoughtsharingapp.classes.NotificationStarter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PostCommentActivity extends AppCompatActivity {

    public static final String PEOPLE_POSTS_THOUGHTS = "thoughts";
    private static final String TAG = PostCommentActivity.class.getSimpleName();

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Feed, MessageViewHolder> mFirebaseAdapter;

    private ImageButton mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;
    private String mEmail = "Anonymous";

    //This object contains all the information about the post the user clicked on. Let's use to create the private message
    private Feed mPostInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();

        mPostInfo = new Feed( intent.getStringExtra(MainActivity.POST_TEXT_EXTRAS), intent.getStringExtra(MainActivity.USER_ID_EXTRAS), intent.getStringExtra(MainActivity.POST_ID_EXTRAS));

        mMessageEditText = findViewById(R.id.messageEditText);
        mMessageRecyclerView = findViewById(R.id.messages_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        // Initialize Firebase Auth
        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseAuth.getUid() != null) {
                    mEmail = mFirebaseAuth.getCurrentUser().getEmail();
                } else {
                    Toast.makeText(PostCommentActivity.this, "Need to sign in", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "database error " + databaseError.getMessage());
            }
        });

        Query query = mFirebaseDatabaseReference.child(PEOPLE_POSTS_THOUGHTS).child(mFirebaseAuth.getUid()).
                child(mPostInfo.getPostId());
        FirebaseRecyclerOptions<Feed> options = new FirebaseRecyclerOptions.Builder<Feed>()
                .setQuery(query, Feed.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Feed, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(MessageViewHolder viewHolder, int position, Feed feed) {
                viewHolder.messageTextView.setText(feed.getPostText());

            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thoughts friendlyMessage = new Thoughts(mMessageEditText.getText().toString(), mFirebaseAuth.getUid());
                mFirebaseDatabaseReference.child(PEOPLE_POSTS_THOUGHTS).child(mPostInfo.getUserId()).child(mPostInfo.getPostId()).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
        NotificationStarter notification = new NotificationStarter(this);
        notification.checkForNewRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.stopListening();
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout row;
        TextView messageTextView;

        MessageViewHolder(View v) {
            super(v);
            row = itemView.findViewById(R.id.row);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}