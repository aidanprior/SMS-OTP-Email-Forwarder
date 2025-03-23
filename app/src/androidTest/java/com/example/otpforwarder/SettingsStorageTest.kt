package com.example.otpforwarder

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SettingsStorageTest {

    private lateinit var context: Context
    private lateinit var settingsStorage: SettingsStorage
    
    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        settingsStorage = SettingsStorage(context)
        
        // Clear any existing settings
        settingsStorage.clearAllSettings()
    }
    
    @Test
    fun testSaveAndRetrieveEmail() {
        val testEmail = "test@example.com"
        
        // Save email
        settingsStorage.saveEmail(testEmail)
        
        // Retrieve email
        val retrievedEmail = settingsStorage.getEmail()
        
        // Verify
        assertEquals(testEmail, retrievedEmail)
    }
    
    @Test
    fun testSaveAndRetrieveServiceState() {
        // Default should be false
        assertFalse(settingsStorage.isServiceEnabled())
        
        // Enable service
        settingsStorage.setServiceEnabled(true)
        
        // Verify enabled
        assertTrue(settingsStorage.isServiceEnabled())
        
        // Disable service
        settingsStorage.setServiceEnabled(false)
        
        // Verify disabled
        assertFalse(settingsStorage.isServiceEnabled())
    }
    
    @Test
    fun testSavingMultipleSettings() {
        val testEmail = "multi@example.com"
        val testKeywords = listOf("OTP", "code", "verification")
        
        // Save settings
        settingsStorage.saveEmail(testEmail)
        settingsStorage.saveKeywords(testKeywords)
        settingsStorage.setServiceEnabled(true)
        
        // Verify all settings
        assertEquals(testEmail, settingsStorage.getEmail())
        assertEquals(testKeywords, settingsStorage.getKeywords())
        assertTrue(settingsStorage.isServiceEnabled())
    }
}
