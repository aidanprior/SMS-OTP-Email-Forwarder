package com.example.otpforwarder;

import android.util.Log;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class to handle email sending using JavaMail API
 */
public class EmailSender {
    private static final String TAG = "EmailSender";
    
    private final String authUsername; // Username for authentication
    private final String fromAddress;  // From address (may include plus addressing)
    private final String password;
    private final Properties properties;
    
    /**
     * Create a new EmailSender
     * 
     * @param authUsername Username for authentication (original email)
     * @param fromAddress Address to use in the From field (may have plus addressing)
     * @param password Email password
     * @param properties SMTP properties
     */
    public EmailSender(String authUsername, String fromAddress, String password, Properties properties) {
        this.authUsername = authUsername;
        this.fromAddress = fromAddress;
        this.password = password;
        this.properties = properties;
    }
    
    /**
     * Backwards compatibility constructor that uses the same email for auth and from
     */
    public EmailSender(String username, String password, Properties properties) {
        this(username, username, password, properties);
    }
    
    /**
     * Verify email credentials by connecting to the SMTP server
     * without actually sending an email
     * 
     * @return An EmailResult object containing success status and error details if any
     */
    public EmailResult verifyCredentials() {
        try {
            Log.d(TAG, "Verifying email credentials for " + authUsername);
            
            // Debug SMTP properties
            for (Object key : properties.keySet()) {
                Log.d(TAG, "SMTP Property: " + key + " = " + properties.getProperty(key.toString()));
            }
            
            // Create email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUsername, password);
                }
            });
            
            // Enable debug mode for more detailed logging
            session.setDebug(true);
            
            // Just connect to verify credentials without sending an email
            Transport transport = session.getTransport("smtp");
            transport.connect(properties.getProperty("mail.smtp.host"), 
                             authUsername, 
                             password);
            transport.close();
            
            Log.d(TAG, "Email credentials verified successfully");
            return new EmailResult(true, null);
            
        } catch (AuthenticationFailedException e) {
            String errorMsg = "Authentication failed. Check your email and password. " + 
                    "Some providers require app-specific passwords or special settings.";
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
            
        } catch (MessagingException e) {
            String errorMsg = "Email verification failed: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
            
        } catch (Exception e) {
            String errorMsg = "Unexpected error while verifying email: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
        }
    }
    
    /**
     * Send an email with the given subject and body to the recipient
     * 
     * @param recipient Email address of the recipient
     * @param subject Email subject
     * @param body Email body content
     * @return An EmailResult object containing success status and error details if any
     */
    public EmailResult sendEmail(String recipient, String subject, String body) {
        try {
            Log.d(TAG, "Preparing to send email to " + recipient);
            Log.d(TAG, "Using sender email: " + fromAddress + " (auth: " + authUsername + ")");
            
            // Debug SMTP properties
            for (Object key : properties.keySet()) {
                Log.d(TAG, "SMTP Property: " + key + " = " + properties.getProperty(key.toString()));
            }
            
            // Create email session with authentication (use authUsername)
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUsername, password);
                }
            });
            
            // Enable debug mode for more detailed logging
            session.setDebug(true);
            
            // Create MimeMessage using fromAddress for the From field
            Message message = new MimeMessage(session);
            
            // For Yahoo accounts, always use the exact authenticated email in the From field
            if (authUsername.toLowerCase().contains("yahoo")) {
                message.setFrom(new InternetAddress(authUsername));
            } else {
                message.setFrom(new InternetAddress(fromAddress));
            }
            
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);
            
            // Send the message
            Transport.send(message);
            
            Log.d(TAG, "Email sent successfully to " + recipient);
            return new EmailResult(true, null);
            
        } catch (AuthenticationFailedException e) {
            String errorMsg = "Authentication failed. Check your email and password. " + 
                    "Some providers require app-specific passwords or special settings.";
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
            
        } catch (MessagingException e) {
            String errorMsg = "Email sending failed: " + e.getMessage();
            // Check for specific Yahoo errors
            if (authUsername.toLowerCase().contains("yahoo") && 
                (e.getMessage().contains("Mailbox unavailable") || 
                 e.getMessage().contains("Request failed"))) {
                errorMsg = "Yahoo requires the sender email to exactly match the authenticated account. " +
                           "Please check your settings and try again.";
            }
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while sending email: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            return new EmailResult(false, errorMsg);
        }
    }
    
    /**
     * Result class for email sending operations
     */
    public static class EmailResult {
        private final boolean success;
        private final String errorMessage;
        
        public EmailResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
