package com.example.otpforwarder.detection;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to detect OTP codes from SMS messages using various pattern recognition techniques.
 */
public class OTPDetectionService {
    private static final String TAG = "OTPDetectionService";
    
    // List of pattern detectors for different types of OTPs
    private final List<OTPPatternDetector> patternDetectors;
    
    // Confidence threshold (0-100) for OTP detection
    private static final int DEFAULT_CONFIDENCE_THRESHOLD = 70;
    private int confidenceThreshold = DEFAULT_CONFIDENCE_THRESHOLD;
    
    public OTPDetectionService() {
        patternDetectors = new ArrayList<>();
        initializePatternDetectors();
    }
    
    /**
     * Initialize the pattern detectors with common OTP patterns
     */
    private void initializePatternDetectors() {
        // Standard numeric OTP patterns (4-8 digits)
        patternDetectors.add(new OTPPatternDetector(
                "\\b([0-9]{4,8})\\b", 
                new String[]{"code", "otp", "verification", "verify", "passcode", "security", "auth", "authenticate", "confirmation"},
                80));
        
        // Banking OTP patterns often with specific prefixes
        patternDetectors.add(new OTPPatternDetector(
                "\\b([0-9]{6})\\b", 
                new String[]{"bank", "transaction", "account", "banking", "debit", "credit", "payment", "transfer"},
                85));
        
        // Alphanumeric OTP patterns (common for some services)
        patternDetectors.add(new OTPPatternDetector(
                "\\b([A-Za-z0-9]{6,8})\\b", 
                new String[]{"code", "verification", "verify", "access"},
                75));
        
        // OTPs with specific formatting (like XXX-XXX)
        patternDetectors.add(new OTPPatternDetector(
                "\\b([0-9]{3})[\\s-]([0-9]{3})\\b", 
                new String[]{"code", "verification", "passcode"},
                80));
    }
    
    /**
     * Analyzes a message to detect if it contains an OTP code
     * 
     * @param sender The sender of the message (phone number or name)
     * @param messageBody The body text of the message
     * @return A detection result with the OTP code and confidence score
     */
    public OTPDetectionResult detectOTP(String sender, String messageBody) {
        Log.d(TAG, "Starting OTP detection for message: " + messageBody);
        
        if (messageBody == null || messageBody.isEmpty()) {
            Log.d(TAG, "Empty message, skipping OTP detection");
            return new OTPDetectionResult(null, 0, "Empty message");
        }
        
        // Normalize the message for better matching
        String normalizedMessage = messageBody.toLowerCase();
        
        // Find the best OTP match based on patterns and context
        OTPDetectionResult bestResult = null;
        int highestConfidence = 0;
        
        for (OTPPatternDetector detector : patternDetectors) {
            OTPDetectionResult result = detector.detect(sender, normalizedMessage);
            
            if (result.getConfidence() > highestConfidence) {
                highestConfidence = result.getConfidence();
                bestResult = result;
            }
        }
        
        // If no detector found a match, try a generic fallback detection
        if (bestResult == null || bestResult.getOtpCode() == null) {
            bestResult = performFallbackDetection(sender, normalizedMessage);
        }
        
        // Apply additional context-based confidence adjustments
        if (bestResult != null && bestResult.getOtpCode() != null) {
            int adjustedConfidence = adjustConfidenceBasedOnContext(sender, normalizedMessage, bestResult);
            bestResult = new OTPDetectionResult(
                    bestResult.getOtpCode(),
                    adjustedConfidence,
                    bestResult.getDescription()
            );
        }
        
        Log.d(TAG, "OTP detection result: " + (bestResult != null ? bestResult.toString() : "No OTP detected"));
        return bestResult != null ? bestResult : new OTPDetectionResult(null, 0, "No OTP detected");
    }
    
    /**
     * Fallback detection method when no specific pattern matches well
     */
    private OTPDetectionResult performFallbackDetection(String sender, String messageBody) {
        Log.d(TAG, "Performing fallback OTP detection");
        
        // Generic OTP/verification detection - look for any sequence of 4-8 digits
        // that might be an OTP, with lower confidence
        Pattern fallbackPattern = Pattern.compile("\\b([0-9]{4,8})\\b");
        Matcher matcher = fallbackPattern.matcher(messageBody);
        
        if (matcher.find()) {
            String potentialOtp = matcher.group(1);
            
            // Assign base confidence score (lower than specific patterns)
            int confidence = 50;
            
            // Check for common OTP phrases
            String[] otpPhrases = {"code", "otp", "verification", "password", "passcode", "pin"};
            for (String phrase : otpPhrases) {
                if (messageBody.contains(phrase)) {
                    confidence += 10;
                    break;
                }
            }
            
            return new OTPDetectionResult(potentialOtp, confidence, "Generic pattern match");
        }
        
        return new OTPDetectionResult(null, 0, "No OTP detected");
    }
    
    /**
     * Adjust confidence score based on additional context factors
     */
    private int adjustConfidenceBasedOnContext(String sender, String messageBody, OTPDetectionResult result) {
        int adjustedConfidence = result.getConfidence();
        
        // Known sender patterns for OTPs increase confidence
        if (isSenderTrusted(sender)) {
            adjustedConfidence += 10;
        }
        
        // Message length - OTP messages tend to be short
        if (messageBody.length() < 160) {
            adjustedConfidence += 5;
        }
        
        // Timing words suggest urgency of OTP
        if (messageBody.contains("expire") || 
            messageBody.contains("valid") || 
            messageBody.contains("minute")) {
            adjustedConfidence += 5;
        }
        
        // Ensure confidence doesn't exceed 100
        return Math.min(adjustedConfidence, 100);
    }
    
    /**
     * Check if sender appears to be a trusted OTP source
     */
    private boolean isSenderTrusted(String sender) {
        if (sender == null) return false;
        
        // Normalize sender
        String normalizedSender = sender.toLowerCase();
        
        // Common OTP sender patterns
        String[] trustedPatterns = {
            "verification", "verify", "secure", "auth", "bank", "info", "alert",
            "noreply", "no-reply", "service"
        };
        
        for (String pattern : trustedPatterns) {
            if (normalizedSender.contains(pattern)) {
                return true;
            }
        }
        
        // Short numeric senders are often automated systems
        if (normalizedSender.matches("^[0-9]{3,6}$")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if the detection result confidence exceeds the threshold
     */
    public boolean isOTPDetected(OTPDetectionResult result) {
        return result != null && 
               result.getOtpCode() != null && 
               result.getConfidence() >= confidenceThreshold;
    }
    
    /**
     * Set a custom confidence threshold
     */
    public void setConfidenceThreshold(int threshold) {
        if (threshold >= 0 && threshold <= 100) {
            this.confidenceThreshold = threshold;
        }
    }
    
    /**
     * Get the current confidence threshold
     */
    public int getConfidenceThreshold() {
        return confidenceThreshold;
    }
}
