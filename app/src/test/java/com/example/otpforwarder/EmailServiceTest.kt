package com.example.otpforwarder

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.*
import java.util.concurrent.Executor

class EmailServiceTest {
    
    private lateinit var emailService: EmailService
    private lateinit var mockExecutor: Executor
    
    @Before
    fun setup() {
        mockExecutor = mock(Executor::class.java)
        emailService = EmailService(mockExecutor)
    }
    
    @Test
    fun `test email creation with valid parameters`() {
        val recipient = "test@example.com"
        val subject = "OTP Code"
        val messageBody = "Your OTP is 123456"
        
        val email = emailService.createEmail(recipient, subject, messageBody)
        
        assertEquals(recipient, email.recipientEmail)
        assertEquals(subject, email.subject)
        assertEquals(messageBody, email.body)
    }
    
    @Test
    fun `test validate email format`() {
        assertTrue(emailService.isValidEmail("user@example.com"))
        assertTrue(emailService.isValidEmail("name.surname@domain.co.uk"))
        assertFalse(emailService.isValidEmail("invalid-email"))
        assertFalse(emailService.isValidEmail("@domain.com"))
    }
    
    @Test
    fun `test email subject formatting`() {
        val sender = "AMAZON"
        val otp = "123456"
        
        val subject = emailService.formatSubject(sender, otp)
        
        assertEquals("OTP from AMAZON: 123456", subject)
    }
}
