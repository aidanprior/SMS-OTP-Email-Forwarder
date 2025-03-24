package com.example.otpforwarder.detection;

/**
 * Represents the result of an OTP detection operation
 */
public class OTPDetectionResult {
    private final String otpCode;
    private final int confidence;
    private final String description;
    
    public OTPDetectionResult(String otpCode, int confidence, String description) {
        this.otpCode = otpCode;
        this.confidence = confidence;
        this.description = description;
    }
    
    /**
     * @return the detected OTP code, or null if none found
     */
    public String getOtpCode() {
        return otpCode;
    }
    
    /**
     * @return confidence score (0-100) indicating how likely this is an OTP
     */
    public int getConfidence() {
        return confidence;
    }
    
    /**
     * @return description of the detection process
     */
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "OTPDetectionResult{" +
                "otpCode='" + otpCode + '\'' +
                ", confidence=" + confidence +
                ", description='" + description + '\'' +
                '}';
    }
}
