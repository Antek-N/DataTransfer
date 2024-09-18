package com.example.datatransfer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    // Tag used for logging messages
    private static final String TAG = "FCMService";
    // Channel ID for the notification
    private static final String CHANNEL_ID = "default_channel_id";
    // ID for the notification
    private static final int NOTIFICATION_ID = 1;


    // Called when a message is received from Firebase Cloud Messaging.
    // Logs the sender and shows a notification with the message details.
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");

        if (title != null && message != null) {
            showNotification(title, message);
        }
    }

    // Called when a new token is generated for the device.
    // Logs the new token which can be used to send messages to this device.
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    // Displays a notification with the given title and message.
    // Creates the notification channel if needed, and builds and displays the notification.
    private void showNotification(String title, String message) {
        createNotificationChannelIfNeeded();
        PendingIntent pendingIntent = createPendingIntent(message);
        NotificationCompat.Builder notificationBuilder = buildNotification(title, message, pendingIntent);
        displayNotification(notificationBuilder);
    }

    // Creates a notification channel if the device is running Android O or later.
    // This is required for notifications to work on Android 8.0 and above.
    private void createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for default notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Creates a PendingIntent to handle the copying message.
    // Returns a PendingIntent for broadcasting the message content
    private PendingIntent createPendingIntent(String message) {
        Intent intent = new Intent(this, CopyBroadcastReceiver.class);
        intent.putExtra("message", message);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }

        return PendingIntent.getBroadcast(this, 0, intent, flags);
    }

    // Builds the notification with the provided title, message, and PendingIntent for the action.
    // Returns a NotificationCompat.Builder object configured with all details.
    private NotificationCompat.Builder buildNotification(String title, String message, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_app)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_app, "Copy", pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.teal_700))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    }

    // Displays the notification using NotificationManagerCompat.
    // Checks if the notification permission is granted before displaying the notification.
    private void displayNotification(NotificationCompat.Builder notificationBuilder) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Notification permission not granted");
            return;
        }

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
