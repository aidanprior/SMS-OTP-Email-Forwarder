package com.example.otpforwarder.detection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detector for a specific OTP pattern
 */
public class OTPPatternDetector {
    private final Pattern pattern;
    private final String[] contextKeywords;
    private final int baseConfidence;
    
    /**
     * Create a new OTP pattern detector
     *
     * @param regex The regular expression pattern to match OTP codes
     * @param contextKeywords Keywords that increase confidence when found in the message
     * @param baseConfidence The base confidence level for this pattern (0-100)
     */
    public OTPPatternDetector(String regex, String[] contextKeywords, int baseConfidence) {
        this.pattern = Pattern.compile(regex);
        this.contextKeywords = contextKeywords;
        this.baseConfidence = baseConfidence;
    }
    
    /**
     * Detect OTP codes in a message
     *
     * @param sender The message sender
     * @param message The message content
     * @return Detection result with OTP code and confidence
     */
    public OTPDetectionResult detect(String sender, String message) {
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            // Extract the OTP code
            String otpCode;
            if (matcher.groupCount() > 1) {
                // If the pattern has multiple groups, combine them
                StringBuilder combined = new StringBuilder();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    combined.append(matcher.group(i));
                }
                otpCode = combined.toString();
            } else {
                otpCode = matcher.group(1);
            }
            
            // Calculate confidence score
            int confidence = baseConfidence;
            
            // Check for context keywords
            for (String keyword : contextKeywords) {
                if (message.contains(keyword)) {
                    confidence += 5;
                    break; // Only add the bonus once
                }
            }
            
            return new OTPDetectionResult(otpCode, confidence, "Pattern match: " + pattern.pattern());
        }
        
        return new OTPDetectionResult(null, 0, "No match for pattern: " + pattern.pattern());
    }
}
