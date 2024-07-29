package com.example.datatransfer;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CopyReceiver extends BroadcastReceiver {

    // This method is called when the BroadcastReceiver receives an Intent broadcast.
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the intent is not null and has the extra data with the key "text_to_copy"
        if (intent != null && intent.hasExtra("text_to_copy")) {
            // Retrieve the text to be copied from the intent
            String textToCopy = intent.getStringExtra("text_to_copy");

            // Get the ClipboardManager system service to handle clipboard operations
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a new ClipData object with the text to be copied
            ClipData clip = ClipData.newPlainText("copied_text", textToCopy);

            // Set the ClipData object as the primary clip on the clipboard
            clipboard.setPrimaryClip(clip);

            // Show a toast message to notify the user that the text has been copied
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
