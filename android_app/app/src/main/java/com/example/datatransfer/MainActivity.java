package com.example.datatransfer;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // Constants for notification channel ID and request code
    private static final String CHANNEL_ID_NOTIFICATION = "CHANNEL_ID_NOTIFICATION";
    private static final int NOTIFICATION_REQUEST_CODE = 101;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the user interface elements
        initializeUI();

        // Check and request notification permission if necessary
        checkAndRequestNotificationPermission();
    }

    // Method to initialize UI components
    private void initializeUI() {
        button = findViewById(R.id.btnNotifications);
        button.setOnClickListener(view -> makeNotification());
    }

    // Method to check and request notification permission
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);
            }
        }
    }

    // Method to create and display a notification
    private void makeNotification() {
        NotificationCompat.Builder builder = createNotificationBuilder();

        // Set the content intent for the notification
        PendingIntent pendingIntent = createContentIntent();
        builder.setContentIntent(pendingIntent);

        // Add a copy action to the notification
        PendingIntent copyPendingIntent = createCopyActionIntent();
        builder.addAction(R.drawable.ic_notifications, "Copy Text", copyPendingIntent);

        // Get the notification manager and create the notification channel
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);

        // Display the notification
        notificationManager.notify(0, builder.build());
    }

    // Method to create a NotificationCompat.Builder object
    private NotificationCompat.Builder createNotificationBuilder() {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Notification Title")
                .setContentText("Some text for notification here")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    // Method to create a PendingIntent for the content of the notification
    private PendingIntent createContentIntent() {
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "some value to be passed here");
        return PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    // Method to create a PendingIntent for the copy action
    private PendingIntent createCopyActionIntent() {
        Intent copyIntent = new Intent(getApplicationContext(), CopyReceiver.class);
        copyIntent.putExtra("text_to_copy", "Thiss_is_the_text_to_copy");
        return PendingIntent.getBroadcast(getApplicationContext(), 0, copyIntent, PendingIntent.FLAG_MUTABLE);
    }

    // Method to create a notification channel (required for Android O and above)
    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID_NOTIFICATION);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID_NOTIFICATION, "some description", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
