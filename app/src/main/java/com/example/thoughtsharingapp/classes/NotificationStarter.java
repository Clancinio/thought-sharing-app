package com.example.thoughtsharingapp.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.thoughtsharingapp.R;
import com.example.thoughtsharingapp.RequestActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationStarter {

    //Notifications channel Id
    public static final String CHANNEL_ID = "Messages";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private Context context;
    private FirebaseAuth auth;
    private DatabaseReference dbRequestReceived;



    public NotificationStarter(Context context) {
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        dbRequestReceived = FirebaseDatabase.getInstance().getReference().child(RequestActivity.FRIEND_REQEST_REFERENCE).child(RequestActivity.REQEST_RECEIVED_REFERENCE);
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        mNotifyManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Messages", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Received a request");
            mNotifyManager.createNotificationChannel(notificationChannel);



        }
    }

    /**
     * This method triggers the actual notification
     */
    private void sendNotification(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }




    private NotificationCompat.Builder getNotificationBuilder(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "New Messages");
        mBuilder.setSmallIcon(R.drawable.ic_anonymous_person)
                .setContentTitle("New Request From Unknown")
                .setContentText("You have received a new request. would you like to reply" )
                .setChannelId(CHANNEL_ID);

        return  mBuilder;
    }

    /**
     * This method helps you to check when you get a new request and send a notification.
     *
     * NOTE: You always want to use this when you need to listen for new incoming request.
     */
    public void checkForNewRequest(){

        dbRequestReceived.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sendNotification();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
