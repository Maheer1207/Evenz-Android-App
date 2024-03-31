package com.example.evenz;

import android.app.NotificationChannel;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// THIS IS FOR RECIEVING NOTIFICATIONS.
public class PushNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("BEN4444", "From: " + remoteMessage.getFrom());
        Log.d("BEN4444", "From: " + remoteMessage.getData());
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
