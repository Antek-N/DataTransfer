package com.example.datatransfer;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CopyBroadcastReceiver extends BroadcastReceiver {

    // Overrides the onReceive method to extract the message, copy it to the clipboard,
    // and display a toast notification informing the user that the content has been copied
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.hasExtra("message")) {
            String message = intent.getStringExtra("message");

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("FCM Notification", message);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Notification content copied", Toast.LENGTH_SHORT).show();
        }
    }
}
