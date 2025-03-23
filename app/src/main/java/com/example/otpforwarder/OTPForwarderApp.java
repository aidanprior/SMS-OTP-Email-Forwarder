package com.example.otpforwarder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPForwarderApp extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private EditText emailEditText;
    private EditText senderEmailEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "OTPForwarderPrefs";
    private static final String KEY_RECIPIENT_EMAIL = "recipient_email";
    private static final String KEY_SENDER_EMAIL = "sender_email";
    private static final String KEY_SENDER_PASSWORD = "sender_password";
    private static final String TAG = "OTPForwarderApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        emailEditText = findViewById(R.id.emailEditText);
        senderEmailEditText = findViewById(R.id.senderEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        
        emailEditText.setText(preferences.getString(KEY_RECIPIENT_EMAIL, ""));
        senderEmailEditText.setText(preferences.getString(KEY_SENDER_EMAIL, ""));
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(OTPForwarderApp.this, "Settings saved!", Toast.LENGTH_SHORT).show();
            }
        });
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);
        }
        
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new SMSReceiver(), filter);
    }
    
    private void saveSettings() {
        String password = passwordEditText.getText().toString().trim();
        // Use a simple placeholder encryption for now
        String encryptedPassword = SecurityUtils.encrypt(this, password);
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_RECIPIENT_EMAIL, emailEditText.getText().toString().trim());
        editor.putString(KEY_SENDER_EMAIL, senderEmailEditText.getText().toString().trim());
        editor.putString(KEY_SENDER_PASSWORD, encryptedPassword);
        editor.apply();
    }
    
    public class SMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Basic SMS receiver implementation
            Log.d(TAG, "SMS received");
            // Implementation details would go here
        }
    }
}
