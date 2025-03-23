package com.example.otpforwarder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SmsReceiverTest {

    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockIntent: Intent
    
    @Mock
    private lateinit var mockSmsMessage: SmsMessage
    
    @Mock
    private lateinit var mockBundle: Bundle
    
    private lateinit var smsReceiver: SmsReceiver
    
    @Before
    fun setup() {
        smsReceiver = SmsReceiver()
        
        // Mock SmsMessage creation
        val pdus = arrayOf<Any>(byteArrayOf())
        `when`(mockBundle.get("pdus")).thenReturn(pdus)
        `when`(mockIntent.extras).thenReturn(mockBundle)
        
        // Create a reflection implementation to mock the static method
        mockkStatic(SmsMessage::class)
        `when`(SmsMessage.createFromPdu(any(ByteArray::class.java), anyString())).thenReturn(mockSmsMessage)
    }
    
    @Test
    fun `test handle SMS with OTP`() {
        `when`(mockSmsMessage.messageBody).thenReturn("Your verification code is 123456")
        `when`(mockSmsMessage.originatingAddress).thenReturn("12345")
        
        // Create a spy on the receiver to verify method calls
        val spyReceiver = spy(smsReceiver)
        
        spyReceiver.onReceive(mockContext, mockIntent)
        
        // Verify that the forwardOtpByEmail method was called with correct parameters
        verify(spyReceiver).forwardOtpByEmail(eq(mockContext), eq("12345"), eq("123456"))
    }
    
    @Test
    fun `test ignore non-OTP SMS`() {
        `when`(mockSmsMessage.messageBody).thenReturn("This is a regular message")
        
        // Create a spy on the receiver to verify method calls
        val spyReceiver = spy(smsReceiver)
        
        spyReceiver.onReceive(mockContext, mockIntent)
        
        // Verify forwardOtpByEmail was never called
        verify(spyReceiver, never()).forwardOtpByEmail(any(), any(), any())
    }
}
