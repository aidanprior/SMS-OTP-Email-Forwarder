package com.example.otpforwarder;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;

import com.example.otpforwarder.detection.OTPDetectionResult;
import com.example.otpforwarder.detection.OTPDetectionService;

import java.util.Properties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.AuthenticationFailedException;

public class OTPForwarderApp extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "OTPForwarderPrefs";
    private static final String KEY_RECIPIENT_EMAIL = "recipient_email";
    private static final String KEY_SENDER_EMAIL = "sender_email";
    private static final String KEY_SENDER_PASSWORD = "sender_password";
    private static final String KEY_SEND_TO_SELF = "send_to_self";
    private static final String TAG = "OTPForwarderApp";
    
    // For plus addressing
    private static final String SENDER_PLUS_SUFFIX = "SMSOTPForwarder";
    private static final String RECIPIENT_PLUS_SUFFIX = "SMSOTP";
    
    // Remove duplicate message ID tracking constants
    
    // UI elements
    private EditText emailEditText;
    private EditText senderEmailEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private TextView statusTextView;
    private SwitchCompat sendToSelfSwitch;
    private TextView emailLabel;
    private TextView senderLabel;
    private ImageView serviceStatusIcon;
    private TextView serviceStatusText;
    
    private SharedPreferences preferences;
    
    // Handler for posting UI updates from background threads
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // OTP detection service instance
    private OTPDetectionService otpDetectionService;
    
    // Receiver for UI updates from the standalone SMS receiver
    private BroadcastReceiver uiUpdateReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Configure log behavior based on build type
        if (!BuildConfig.ENABLE_LOGGING) {
            // In release builds, disable verbose logging
            Log.i(TAG, "Release build - verbose logging disabled");
        }
        
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText);
        senderEmailEditText = findViewById(R.id.senderEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        statusTextView = findViewById(R.id.statusTextView);
        sendToSelfSwitch = findViewById(R.id.sendToSelfSwitch);
        emailLabel = findViewById(R.id.emailLabel);
        senderLabel = findViewById(R.id.senderLabel);
        serviceStatusIcon = findViewById(R.id.serviceStatusIcon);
        serviceStatusText = findViewById(R.id.serviceStatusText);
        
        // Set service status to inactive initially
        updateServiceStatus(false);
        
        // Load saved settings
        boolean sendToSelf = preferences.getBoolean(KEY_SEND_TO_SELF, true);
        sendToSelfSwitch.setChecked(sendToSelf);
        updateEmailFieldsVisibility(sendToSelf);
        emailEditText.setText(preferences.getString(KEY_RECIPIENT_EMAIL, ""));
        senderEmailEditText.setText(preferences.getString(KEY_SENDER_EMAIL, ""));
        
        // Set up send to self switch listener
        sendToSelfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateEmailFieldsVisibility(isChecked);
            }
        });
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveSettings();
                    // Verify credentials immediately after saving
                    verifyEmailCredentials();
                }
            }
        });
        
        // Check for and request required permissions
        checkAndRequestPermissions();
        
        // Initialize the OTP detection service
        otpDetectionService = new OTPDetectionService();
        
        // Set initial status text
        updateStatusText("Enter your email settings and save to activate the service.");
        
        // Register receiver for UI updates from background
        uiUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String statusMessage = intent.getStringExtra("status_message");
                if (statusMessage != null) {
                    updateStatusText(statusMessage);
                }
                
                // Update service status if notification included
                if (intent.hasExtra("service_active")) {
                    boolean active = intent.getBooleanExtra("service_active", false);
                    updateServiceStatus(active);
                }
            }
        };
        registerReceiverCompat(uiUpdateReceiver, new IntentFilter("com.example.otpforwarder.UI_UPDATE"));
        
        // Check if service should be active based on credentials
        checkServiceStatus();
    }
    
    // Add this method to handle different Android versions
    private void registerReceiverCompat(BroadcastReceiver receiver, IntentFilter filter) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14 (API 34) and above - Use explicit flag
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // Older Android versions - Use old API
            registerReceiver(receiver, filter);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Unregister receivers in one try-catch block
        try {
            if (uiUpdateReceiver != null) {
                unregisterReceiver(uiUpdateReceiver);
            }
        } catch (Exception e) {
            // Receiver might not be registered, that's ok
            Log.d(TAG, "Error unregistering receivers: " + e.getMessage());
        }
    }
    
    private boolean validateInputs() {
        boolean sendToSelf = sendToSelfSwitch.isChecked();
        String recipientEmail = emailEditText.getText().toString().trim();
        String senderEmail;
        
        if (sendToSelf) {
            senderEmail = recipientEmail; // Use same email for sender and recipient
        } else {
            senderEmail = senderEmailEditText.getText().toString().trim();
            // Validate sender email if not in self-send mode
            if (senderEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (!Patterns.EMAIL_ADDRESS.matcher(senderEmail).matches()) {
                Toast.makeText(this, "Invalid sender email format", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        
        String password = passwordEditText.getText().toString().trim();
        
        if (recipientEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches()) {
            Toast.makeText(this, "Invalid recipient email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void saveSettings() {
        boolean sendToSelf = sendToSelfSwitch.isChecked();
        String recipientEmail = emailEditText.getText().toString().trim();
        String senderEmail;
        
        if (sendToSelf) {
            senderEmail = recipientEmail; // Use same email for sender and recipient
        } else {
            senderEmail = senderEmailEditText.getText().toString().trim();
        }
        
        String password = passwordEditText.getText().toString().trim();
        // Use a simple placeholder encryption for now
        String encryptedPassword = SecurityUtils.encrypt(this, password);
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_RECIPIENT_EMAIL, recipientEmail);
        editor.putString(KEY_SENDER_EMAIL, senderEmail);
        editor.putString(KEY_SENDER_PASSWORD, encryptedPassword);
        editor.putBoolean(KEY_SEND_TO_SELF, sendToSelf);
        editor.apply();
        
        Toast.makeText(OTPForwarderApp.this, "Settings saved!", Toast.LENGTH_SHORT).show();
        updateStatusText("Settings saved. Verifying credentials...");
        
        Log.d(TAG, "Settings saved for email: " + recipientEmail);
    }
    
    private void verifyEmailCredentials() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get email credentials from shared preferences
                    final String senderEmail = preferences.getString(KEY_SENDER_EMAIL, "");
                    String senderPassword = preferences.getString(KEY_SENDER_PASSWORD, "");
                    
                    // Decrypt the password
                    senderPassword = SecurityUtils.decrypt(OTPForwarderApp.this, senderPassword);
                    
                    // Determine SMTP settings based on email domain
                    String domain = EmailUtils.extractDomain(senderEmail);
                    EmailUtils.SmtpSettings smtpSettings = EmailUtils.getSmtpSettings(domain);
                    
                    // Set email properties
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", smtpSettings.host);
                    props.put("mail.smtp.port", smtpSettings.port);
                    
                    Log.d(TAG, "Attempting to verify SMTP credentials for: " + senderEmail);
                    Log.d(TAG, "Using SMTP server: " + smtpSettings.host + ":" + smtpSettings.port);
                    
                    // Store final reference to password for use in anonymous inner class
                    final String finalSenderPassword = senderPassword;
                    
                    // Create a mail session
                    final Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, finalSenderPassword);
                        }
                    });
                    
                    // Connect to server without sending email (just verify credentials)
                    Transport transport = session.getTransport("smtp");
                    transport.connect(smtpSettings.host, 
                                      senderEmail, 
                                      finalSenderPassword);
                    transport.close();
                    
                    // If we got here, authentication was successful
                    showResultOnUiThread("Email credentials verified successfully! Service is active.");
                    
                    // Update service status on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateServiceStatus(true);
                        }
                    });
                } catch (AuthenticationFailedException e) {
                    String errorMsg = EmailUtils.getAuthErrorMessage(preferences.getString(KEY_SENDER_EMAIL, ""));
                    Log.e(TAG, "Authentication failed: " + e.getMessage(), e);
                    showErrorOnUiThread("Authentication Failed", errorMsg);
                    
                    // Update service status on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateServiceStatus(false);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error verifying email credentials", e);
                    showErrorOnUiThread("Verification Failed", 
                            "Could not verify email credentials: " + e.getMessage());
                    
                    // Update service status on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateServiceStatus(false);
                        }
                    });
                }
            }
        }).start();
    }
    
    // Keep the inner class for SMTP settings for backward compatibility
    private static class SmtpSettings extends EmailUtils.SmtpSettings {
        public SmtpSettings(String host, String port) {
            super(host, port);
        }
    }
    
    private void showResultOnUiThread(final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OTPForwarderApp.this, message, Toast.LENGTH_LONG).show();
                updateStatusText(message);
            }
        });
    }
    
    private void showErrorOnUiThread(final String title, final String errorMessage) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                updateStatusText("Error: " + errorMessage);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(OTPForwarderApp.this);
                builder.setTitle(title)
                       .setMessage(errorMessage)
                       .setPositiveButton("OK", null);
                
                // If this is a Gmail authentication issue, add a button to open App Passwords page
                if (errorMessage.contains("Gmail authentication failed")) {
                    builder.setNeutralButton("Open App Passwords", (dialog, which) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
                                Uri.parse("https://myaccount.google.com/apppasswords"));
                        startActivity(browserIntent);
                    });
                }
                
                builder.show();
            }
        });
    }
    
    private void updateStatusText(String status) {
        if (statusTextView != null) {
            statusTextView.setText(status);
        }
    }
    
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);
            updateStatusText("Waiting for SMS permission...");
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateStatusText("SMS permission granted. Enter your email settings.");
            } else {
                updateStatusText("SMS permission denied. App cannot function properly.");
                Toast.makeText(this, "SMS permission is required for the app to work", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void updateEmailFieldsVisibility(boolean sendToSelf) {
        if (sendToSelf) {
            // Single email mode
            emailLabel.setText("Email Address:");
            senderLabel.setVisibility(View.GONE);
            senderEmailEditText.setVisibility(View.GONE);
        } else {
            // Separate emails mode
            emailLabel.setText("Recipient Email:");
            senderLabel.setVisibility(View.VISIBLE);
            senderEmailEditText.setVisibility(View.VISIBLE);
        }
    }
    
    private void updateServiceStatus(boolean active) {
        if (active) {
            serviceStatusIcon.setImageResource(android.R.drawable.presence_online);
            serviceStatusText.setText("Service Active");
            serviceStatusText.setTextColor(getResources().getColor(R.color.statusActive));
        } else {
            serviceStatusIcon.setImageResource(android.R.drawable.presence_invisible);
            serviceStatusText.setText("Service Inactive");
            serviceStatusText.setTextColor(getResources().getColor(R.color.statusInactive));
        }
    }
    
    /**
     * Check if all required credentials are set up properly to activate the service
     */
    private void checkServiceStatus() {
        String recipientEmail = preferences.getString(KEY_RECIPIENT_EMAIL, "");
        String senderPassword = preferences.getString(KEY_SENDER_PASSWORD, "");
        
        // If we have both email and password set up, we can consider the service active
        boolean hasCredentials = !recipientEmail.isEmpty() && !senderPassword.isEmpty();
        updateServiceStatus(hasCredentials);
        
        if (hasCredentials) {
            updateStatusText("Service is active and monitoring for SMS messages.");
        } else {
            updateStatusText("Enter your email settings and save to activate the service.");
        }
    }
}