package com.example.otpforwarder;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for security operations like encryption/decryption
 */
public class SecurityUtils {
    private static final String TAG = "SecurityUtils";
    private static final String ALGORITHM = "AES";
    
    /**
     * Encrypt the given text using AES encryption algorithm
     * 
     * @param context Context used to derive encryption key
     * @param plainText Text to encrypt
     * @return Encrypted text as Base64 string, or null if encryption failed
     */
    public static String encrypt(Context context, String plainText) {
        try {
            // Generate a key based on the app package name
            SecretKeySpec secretKey = generateKey(context);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during encryption", e);
            return null;
        }
    }
    
    /**
     * Decrypt the given encrypted text using AES decryption algorithm
     * 
     * @param context Context used to derive decryption key
     * @param encryptedText Base64-encoded encrypted text
     * @return Decrypted plaintext, or null if decryption failed
     */
    public static String decrypt(Context context, String encryptedText) {
        try {
            // Generate a key based on the app package name
            SecretKeySpec secretKey = generateKey(context);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during decryption", e);
            return null;
        }
    }
    
    /**
     * Generate a secret key based on the application package name
     * 
     * @param context Context to get the package name
     * @return SecretKeySpec for AES encryption/decryption
     */
    private static SecretKeySpec generateKey(Context context) throws NoSuchAlgorithmException {
        // Use the application package name as the key generation seed
        String seed = context.getPackageName();
        
        // Generate a SHA-256 hash of the seed
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(seed.getBytes(StandardCharsets.UTF_8));
        
        // Use the first 16 bytes (128 bits) for AES key
        byte[] keyBytes = new byte[16];
        System.arraycopy(bytes, 0, keyBytes, 0, 16);
        
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
