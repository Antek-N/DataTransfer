
package com.example.datatransfer;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    TextView textView;

    // This method is called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize the TextView
        textView = findViewById(R.id.textViewData);

        // Retrieve the data passed with the intent
        String data = getIntent().getStringExtra("data");

        // Display the retrieved data in the TextView
        textView.setText(data);
    }
}
