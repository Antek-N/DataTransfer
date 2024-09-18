package com.example.datatransfer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

// Code can work only on Android API 33 (TIRAMISU) or higher
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    // Constants for logging and notification permission
    private static final String TAG = "MainActivity";
    private static final String NOTIFICATION_PERMISSION = android.Manifest.permission.POST_NOTIFICATIONS;
    private static final String FCM_TOKEN_CLIP_LABEL = "FCM Token";
    // UI components
    private TextView tokenTextView;
    private Button copyButton;
    // ActivityResultLauncher for requesting notification permission
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher = createNotificationPermissionLauncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        requestNotificationPermissionIfNeeded();
        fetchAndDisplayFCMToken();
        setupCopyButton();
    }

    // Initializes the UI components (TextView and Button)
    private void initializeUI() {
        tokenTextView = findViewById(R.id.tokenTextView);
        copyButton = findViewById(R.id.copyButton);
    }

    // Creates an ActivityResultLauncher to handle the notification permission request
    private ActivityResultLauncher<String> createNotificationPermissionLauncher() {
        return registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::logPermissionResult);
    }

    // Logs whether the notification permission was granted or denied
    private void logPermissionResult(boolean isGranted) {
        String logMessage = isGranted ? "Notification permission granted" : "Notification permission denied";
        Log.d(TAG, logMessage);
    }

    // Requests notification permission if it hasn't been granted already
    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(NOTIFICATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(NOTIFICATION_PERMISSION);
        }
    }
    // Fetches the Firebase Cloud Messaging (FCM) token and displays it in the UI
    private void fetchAndDisplayFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        displayToken(task.getResult());
                    } else {
                        handleTokenFetchFailure(task.getException());
                    }
                });
    }

    // Displays the FCM token in the TextView and logs it
    private void displayToken(String token) {
        Log.d(TAG, "FCM Token: " + token);
        tokenTextView.setText(token);
    }

    // Handles errors that occur when fetching the FCM token
    private void handleTokenFetchFailure(Exception exception) {
        Log.w(TAG, "Fetching FCM registration token failed", exception);
    }

    // Sets up the copy button to copy the token to the clipboard when clicked
    private void setupCopyButton() {
        copyButton.setOnClickListener(v -> copyTokenToClipboard());
    }

    // Copies the FCM token to the clipboard if it's valid
    private void copyTokenToClipboard() {
        String token = tokenTextView.getText().toString();
        if (isValidToken(token)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(FCM_TOKEN_CLIP_LABEL, token);
            clipboard.setPrimaryClip(clip);
            showToast("Token copied to clipboard");
        } else {
            showToast("Token not available yet");
        }
    }

    // Validates the FCM token by checking if it's non-null, non-empty, and not a placeholder text
    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty() && !"Fetching FCM Token...".equals(token);
    }

    // Displays a toast message on the screen
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
