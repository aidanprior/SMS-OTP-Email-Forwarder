package com.example.otpforwarder;

/**
 * Utility class for email-related operations
 * Contains methods shared between OTPForwarderApp and SMSBroadcastReceiver
 */
public class EmailUtils {

    /**
     * Extract domain from email address
     */
    public static String extractDomain(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * Add plus addressing to an email address if the email provider supports it
     * 
     * @param email The original email address
     * @param suffix The suffix to add after the plus sign
     * @return The email with plus addressing if supported, or the original email
     */
    public static String addPlusAddressing(String email, String suffix) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return email;
        }
        
        String localPart = email.substring(0, email.indexOf('@'));
        String domain = email.substring(email.indexOf('@'));
        
        // Check if the domain supports plus addressing
        if (supportsPlusAddressing(domain)) {
            return localPart + "+" + suffix + domain;
        } else {
            return email;
        }
    }
    
    /**
     * Check if a domain supports plus addressing
     * 
     * @param domain The email domain (including @)
     * @return true if the domain supports plus addressing
     */
    public static boolean supportsPlusAddressing(String domain) {
        // List of domains known to support plus addressing
        String[] supportedDomains = {
            "@gmail.com", 
            "@googlemail.com", 
            "@fastmail.com", 
            "@outlook.com", 
            "@hotmail.com", 
            "@live.com", 
            "@protonmail.com",
            "@pm.me",
            "@zoho.com"
            // Yahoo removed as it has issues with plus addressing when sending
        };
        
        for (String supportedDomain : supportedDomains) {
            if (domain.toLowerCase().equals(supportedDomain)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get SMTP settings for a given email domain
     */
    public static class SmtpSettings {
        public final String host;
        public final String port;
        
        public SmtpSettings(String host, String port) {
            this.host = host;
            this.port = port;
        }
    }
    
    /**
     * Get SMTP settings based on email domain
     */
    public static SmtpSettings getSmtpSettings(String domain) {
        if (domain.equals("gmail.com")) {
            return new SmtpSettings("smtp.gmail.com", "587");
        } else if (domain.equals("outlook.com") || domain.equals("hotmail.com") || domain.equals("live.com")) {
            return new SmtpSettings("smtp.office365.com", "587");
        } else if (domain.equals("yahoo.com") || domain.equals("ymail.com")) {
            return new SmtpSettings("smtp.mail.yahoo.com", "587");
        } else if (domain.equals("aol.com")) {
            return new SmtpSettings("smtp.aol.com", "587");
        } else if (domain.equals("zoho.com")) {
            return new SmtpSettings("smtp.zoho.com", "587");
        } else {
            // Default to Gmail for unknown domains
            return new SmtpSettings("smtp.gmail.com", "587");
        }
    }
    
    /**
     * Get appropriate authentication error message based on email provider
     */
    public static String getAuthErrorMessage(String email) {
        String domain = extractDomain(email);
        
        if (domain.equals("gmail.com")) {
            return "Gmail authentication failed. You need to set up an App Password: " +
                   "https://myaccount.google.com/apppasswords";
        } else if (domain.equals("outlook.com") || domain.equals("hotmail.com")) {
            return "Microsoft account authentication failed. Make sure you've allowed less secure apps " +
                   "or created an app password in your Microsoft account security settings.";
        } else if (domain.equals("yahoo.com")) {
            return "Yahoo authentication failed. You may need to generate an app password " +
                   "in your Yahoo account security settings.";
        } else {
            return "Email authentication failed. Check your password and account settings. " +
                   "Some providers require app-specific passwords or allowing less secure apps.";
        }
    }
}
