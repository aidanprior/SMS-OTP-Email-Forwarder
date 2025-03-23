package com.example.otpforwarder

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class OtpParserTest {
    
    private lateinit var otpParser: OtpParser
    
    @Before
    fun setup() {
        otpParser = OtpParser()
    }
    
    @Test
    fun `test extracting 6 digit OTP code`() {
        val message = "Your verification code is 123456. Do not share this with anyone."
        val result = otpParser.extractOtp(message)
        assertEquals("123456", result)
    }
    
    @Test
    fun `test extracting 4 digit OTP code`() {
        val message = "4321 is your OTP for account verification."
        val result = otpParser.extractOtp(message)
        assertEquals("4321", result)
    }
    
    @Test
    fun `test returns null when no OTP present`() {
        val message = "This message has no OTP code in it."
        val result = otpParser.extractOtp(message)
        assertNull(result)
    }
    
    @Test
    fun `test extracting alphanumeric OTP code`() {
        val message = "Your verification code is A12B34. Valid for 10 minutes."
        val result = otpParser.extractOtp(message)
        assertEquals("A12B34", result)
    }
    
    @Test
    fun `test detects OTP keywords`() {
        val message1 = "Random message with no OTP indicators"
        val message2 = "Your verification code is 123456"
        val message3 = "Your one-time password: 987654"
        
        assertFalse(otpParser.isOtpMessage(message1))
        assertTrue(otpParser.isOtpMessage(message2))
        assertTrue(otpParser.isOtpMessage(message3))
    }
}
