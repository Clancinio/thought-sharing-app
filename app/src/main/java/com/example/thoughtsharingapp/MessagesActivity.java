package com.example.thoughtsharingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MessagesActivity extends AppCompatActivity {

    public static final String MESSAGES_CHILD = "chat";
    private static final String TAG = MessagesActivity.class.getSimpleName();

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

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

        mPostInfo = new Feed(intent.getStringExtra(MainActivity.POST_TEXT), intent.getStringExtra(MainActivity.USER_ID), intent.getStringExtra(MainActivity.POST_ID));

        mMessageEditText = findViewById(R.id.messageEditText);
        mMessageRecyclerView = findViewById(R.id.messages_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        // Initialize Firebase Auth
        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseAuth.getUid() != null) {
                    mEmail = mFirebaseAuth.getCurrentUser().getEmail();
                } else {
                    Toast.makeText(MessagesActivity.this, "Need to sign in", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "database error " + databaseError.getMessage());
            }
        });

        Query query = mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(mPostInfo.getUserId()).child(mPostInfo.getPostId());
        FirebaseRecyclerOptions<FriendlyMessage> options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(query, FriendlyMessage.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(MessageViewHolder viewHolder, int position, FriendlyMessage friendlyMessage) {
                if (!mPostInfo.getUserId().equals(mFirebaseAuth.getUid())) {
                    viewHolder.row.setGravity(Gravity.END);
                } else {
                    viewHolder.row.setGravity(Gravity.START);
                }
                viewHolder.messageTextView.setText(friendlyMessage.getText());
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
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mEmail);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(mPostInfo.getUserId()).child(mPostInfo.getPostId()).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
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