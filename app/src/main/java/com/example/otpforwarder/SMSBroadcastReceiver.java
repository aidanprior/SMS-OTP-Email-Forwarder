package com.example.otpforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.util.Patterns;

import com.example.otpforwarder.detection.OTPDetectionResult;
import com.example.otpforwarder.detection.OTPDetectionService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Standalone broadcast receiver for handling SMS messages even when the app is not running
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSBroadcastReceiver";
    private static final String PREFS_NAME = "OTPForwarderPrefs";
    private static final String KEY_RECIPIENT_EMAIL = "recipient_email";
    private static final String KEY_SENDER_EMAIL = "sender_email";
    private static final String KEY_SENDER_PASSWORD = "sender_password";
    private static final String KEY_SEND_TO_SELF = "send_to_self";
    private static final String SENDER_PLUS_SUFFIX = "SMSOTPForwarder";
    private static final String RECIPIENT_PLUS_SUFFIX = "SMSOTP";
    private static final String KEY_PROCESSED_IDS = "processed_sms_ids";
    private static final int MAX_STORED_IDS = 20;
    
    // Keep track of currently processing message IDs to prevent concurrent processing
    private static final Set<String> PROCESSING_IDS = new HashSet<>();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            return;
        }
        
        Log.d(TAG, "SMS received in standalone receiver");
        
        // Extract the SMS data from the intent
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.d(TAG, "No SMS data in intent");
            return;
        }
        
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0) {
            Log.d(TAG, "No PDUs in intent");
            return;
        }
        
        // Get shared preferences
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Extract the first message to get sender and timestamp
        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0], bundle.getString("format"));
        String sender = sms.getOriginatingAddress();
        long timestamp = sms.getTimestampMillis();
        String messageId = sender + "_" + timestamp;
        
        // Using synchronized to prevent race conditions with message ID checking
        synchronized (PROCESSING_IDS) {
            // Check if this message is already being processed
            if (PROCESSING_IDS.contains(messageId)) {
                Log.d(TAG, "Already processing message: " + messageId);
                return;
            }
            
            // Check if this message has already been processed
            if (isMessageAlreadyProcessed(preferences, messageId)) {
                Log.d(TAG, "Ignoring duplicate SMS message: " + messageId);
                return;
            }
            
            // Mark that we're processing this message
            PROCESSING_IDS.add(messageId);
        }
        
        try {
            // Mark this message as processed in SharedPreferences immediately
            addProcessedMessageId(preferences, messageId);
            
            // Reconstruct the full message from all PDUs
            StringBuilder fullMessageBody = new StringBuilder();
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                fullMessageBody.append(smsMessage.getMessageBody());
            }
            
            String messageBody = fullMessageBody.toString();
            Log.d(TAG, "SMS from: " + sender);
            Log.d(TAG, "Message body: " + messageBody);
            
            // Detect OTP
            OTPDetectionService otpDetectionService = new OTPDetectionService();
            OTPDetectionResult result = otpDetectionService.detectOTP(sender, messageBody);
            
            if (otpDetectionService.isOTPDetected(result)) {
                String otpCode = result.getOtpCode();
                Log.d(TAG, "OTP detected: " + otpCode + " (Confidence: " + result.getConfidence() + "%)");
                
                // Format email and forward
                String emailSubject = "OTP Code Forwarded";
                String emailBody = "OTP Code: " + otpCode + "\n\n" +
                              "Sender: " + sender + "\n" +
                              "Message: " + messageBody + "\n\n" +
                              "Confidence: " + result.getConfidence() + "%\n" +
                              "Detection details: " + result.getDescription();
                
                // Send email in background
                sendEmailInBackground(context, emailSubject, emailBody);
            } else {
                Log.d(TAG, "No OTP detected in SMS");
            }
        } finally {
            // Always remove from processing set when done
            synchronized (PROCESSING_IDS) {
                PROCESSING_IDS.remove(messageId);
            }
        }
    }

    /**
     * Try to update the UI if the app is in foreground - only used for essential service status updates
     * @param context The application context
     * @param message The message to display
     * @param updateStatus Whether to update the service status icon
     */
    private void updateUIIfPossible(Context context, String message, boolean updateStatus) {
        try {
            // Create a broadcast intent that the main activity can listen for
            Intent uiUpdateIntent = new Intent("com.example.otpforwarder.UI_UPDATE");
            uiUpdateIntent.putExtra("status_message", message);
            
            // Only include service status update when explicitly needed
            if (updateStatus) {
                uiUpdateIntent.putExtra("service_active", true);
            }
            
            context.sendBroadcast(uiUpdateIntent);
            Log.d(TAG, "Sent UI update broadcast: " + message);
        } catch (Exception e) {
            // Silently fail - UI updates are optional from the background receiver
            Log.d(TAG, "Could not update UI: " + e.getMessage());
        }
    }
    
    private void sendEmailInBackground(final Context context, final String subject, final String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get credentials from shared preferences
                    SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String recipientEmail = preferences.getString(KEY_RECIPIENT_EMAIL, "");
                    String senderEmail = preferences.getString(KEY_SENDER_EMAIL, "");
                    
                    // If sending to self is enabled, use recipient email as sender
                    boolean sendToSelf = preferences.getBoolean(KEY_SEND_TO_SELF, true);
                    if (sendToSelf && !recipientEmail.isEmpty()) {
                        senderEmail = recipientEmail;
                    }
                    
                    String senderPassword = preferences.getString(KEY_SENDER_PASSWORD, "");
                    
                    // Add plus addressing if supported by the email provider
                    String enhancedSenderEmail = senderEmail;
                    // Only modify sender email if it's not a Yahoo address
                    if (!EmailUtils.extractDomain(senderEmail).contains("yahoo")) {
                        enhancedSenderEmail = EmailUtils.addPlusAddressing(senderEmail, SENDER_PLUS_SUFFIX);
                    }
                    String enhancedRecipientEmail = EmailUtils.addPlusAddressing(recipientEmail, RECIPIENT_PLUS_SUFFIX);
                    
                    Log.d(TAG, "Using sender: " + enhancedSenderEmail + " (original: " + senderEmail + ")");
                    Log.d(TAG, "Using recipient: " + enhancedRecipientEmail + " (original: " + recipientEmail + ")");
                    
                    // Decrypt password
                    senderPassword = SecurityUtils.decrypt(context, senderPassword);
                    
                    // Validate emails
                    if (!Patterns.EMAIL_ADDRESS.matcher(recipientEmail).matches() ||
                        !Patterns.EMAIL_ADDRESS.matcher(senderEmail).matches()) {
                        Log.e(TAG, "Invalid email address format");
                        return;
                    }
                    
                    // Determine SMTP settings based on email domain
                    String domain = EmailUtils.extractDomain(senderEmail);
                    EmailUtils.SmtpSettings smtpSettings = EmailUtils.getSmtpSettings(domain);
                    
                    // Email properties
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", smtpSettings.host);
                    props.put("mail.smtp.port", smtpSettings.port);
                    
                    // Skip verification and go directly to sending
                    // Create email sender instance
                    EmailSender emailSender = new EmailSender(senderEmail, enhancedSenderEmail, 
                                                              senderPassword, props);
                    
                    // Send email directly without verification
                    EmailSender.EmailResult sendResult = emailSender.sendEmail(enhancedRecipientEmail, subject, body);
                    
                    if (sendResult.isSuccess()) {
                        Log.d(TAG, "Email sent successfully from background");
                        // Only log the success, don't update UI for normal operation
                    } else {
                        Log.e(TAG, "Failed to send email from background: " + sendResult.getErrorMessage());
                        // Only update UI for failures that require user attention
                        updateUIIfPossible(context, "Failed to send email: " + sendResult.getErrorMessage(), false);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error sending email from background", e);
                    // Update UI with critical errors
                    updateUIIfPossible(context, "Critical error: " + e.getMessage(), false);
                }
            }
        }).start();
    }
    
    /**
     * Check if we've already processed a message with this ID
     */
    private boolean isMessageAlreadyProcessed(SharedPreferences preferences, String messageId) {
        Set<String> processedIds = preferences.getStringSet(KEY_PROCESSED_IDS, new HashSet<>());
        return processedIds.contains(messageId);
    }
    
    /**
     * Add a message ID to the processed list, removing oldest if needed
     */
    private void addProcessedMessageId(SharedPreferences preferences, String messageId) {
        Set<String> processedIds = new HashSet<>(
                preferences.getStringSet(KEY_PROCESSED_IDS, new HashSet<>()));
        
        // Limit size by removing oldest entries if necessary
        if (processedIds.size() >= MAX_STORED_IDS) {
            // Since HashSet doesn't maintain order, we'll just clear half when it gets too big
            if (processedIds.size() > MAX_STORED_IDS / 2) {
                Iterator<String> iterator = processedIds.iterator();
                for (int i = 0; i < MAX_STORED_IDS / 2; i++) {
                    if (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                }
            }
        }
        
        // Add new message ID
        processedIds.add(messageId);
        
        // Save updated set
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(KEY_PROCESSED_IDS, processedIds);
        editor.apply();
    }
    
    // Keep the inner class for SMTP settings for backward compatibility
    private static class SmtpSettings extends EmailUtils.SmtpSettings {
        public SmtpSettings(String host, String port) {
            super(host, port);
        }
    }
}
