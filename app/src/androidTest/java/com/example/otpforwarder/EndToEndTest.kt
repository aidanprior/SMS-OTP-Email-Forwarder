package com.example.otpforwarder

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

    private lateinit var context: Context
    private lateinit var device: UiDevice
    private lateinit var settingsStorage: SettingsStorage
    private val testEmail = "test@example.com"
    
    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        settingsStorage = SettingsStorage(context)
        
        // Setup app with test configuration
        settingsStorage.saveEmail(testEmail)
        settingsStorage.setServiceEnabled(true)
        
        // Launch the app
        ActivityScenario.launch(MainActivity::class.java)
    }
    
    @Test
    fun testSmsProcessingEndToEnd() {
        // This test requires permissions to insert SMS
        // Note: This requires a rooted device or emulator with special permissions
        
        // Insert a fake SMS with OTP
        insertFakeSms("12345", "Your verification code is 987654")
        
        // Allow time for processing
        val latch = CountDownLatch(1)
        latch.await(5, TimeUnit.SECONDS)
        
        // Check for notification or another observable indication that 
        // the SMS was processed (need to implement in actual app)
        val notification = device.findObject(UiSelector().textContains("987654"))
        assertTrue(notification.exists())
    }
    
    private fun insertFakeSms(sender: String, message: String) {
        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, sender)
            put(Telephony.Sms.BODY, message)
            put(Telephony.Sms.READ, 0)
            put(Telephony.Sms.DATE, System.currentTimeMillis())
        }
        
        try {
            context.contentResolver.insert(Uri.parse("content://sms/inbox"), values)
        } catch (e: SecurityException) {
            // This will likely fail on non-rooted devices
            // For real testing, use a mock SMS receiver that the app can register
        }
        
        // Broadcast a SMS received intent
        val intent = Intent(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        context.sendBroadcast(intent)
    }
}
