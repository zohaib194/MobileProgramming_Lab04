package com.example.zohaibbutt.lab04;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by Zohaib Butt on 14/03/2018.
 */

public class Receiver extends BroadcastReceiver {
    private int oldMessageList;
    private ArrayList<String> newMessageList = new ArrayList<>();
    private ArrayList<String> messagesOwners = new ArrayList<>();
    private String userName, message;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private NotificationChannel channel;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private PendingIntent pendingIntent;
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get the old messages list size from shared pref
        this.preferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        this.oldMessageList = preferences.getInt("OldMessageListSize", 0);

        this.notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        // a pendingIntent for A1 to start again
        Intent i = new Intent(context, A1.class);
        this.pendingIntent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create the NotificationChannel only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.channel = new NotificationChannel(
                    context.getString(R.string.channel_id),
                    context.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(context.getString(R.string.channel_description));

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, channel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        // check for DB change
        root.child("Message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int id = 0;

                if(dataSnapshot.getChildrenCount() > oldMessageList) {
                    // goes through each child of Message
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        // goes through each element of a child of Message
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            // if key is m (Message) adds the message in list
                            if (ds2.getKey().equalsIgnoreCase("m")) {
                                newMessageList.add((String) ds2.getValue());
                            }
                            // if key is u (User) add the username is list
                            if (ds2.getKey().equalsIgnoreCase("u")) {
                                messagesOwners.add((String) ds2.getValue());
                            }
                        }
                    }

                    // Checks how many of messages are new
                    if (newMessageList.size() > oldMessageList) {
                        for (int i = oldMessageList; i < newMessageList.size(); i++) {
                            message = newMessageList.get(i);
                            userName = messagesOwners.get(i);
                            oldMessageList++;
                            showMessageNotification(userName, message, id);
                            id++;
                        }
                        id = 0;
                    }

                    // update the size of old message list for next time receiver fires
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("OldMessageListSize", oldMessageList);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // send the notification
    private void showMessageNotification(String userName, String message, int notificationId){
        // Build the Notification
         this.builder
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(userName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(this.pendingIntent);
        this.notificationManager.notify(notificationId, builder.build());
    }
}
