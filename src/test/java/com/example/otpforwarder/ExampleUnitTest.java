package com.example.otpforwarder;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    
    @Before
    public void setUp() {
        System.out.println("UNIT TEST: Setting up test environment");
    }
    
    @Test
    public void addition_isCorrect() {
        System.out.println("UNIT TEST: Starting addition test");
        assertEquals("2 + 2 should equal 4", 4, 2 + 2);
        System.out.println("UNIT TEST: Addition test completed successfully!");
    }
    
    @Test
    public void subtraction_isCorrect() {
        System.out.println("UNIT TEST: Starting subtraction test");
        assertEquals("5 - 3 should equal 2", 2, 5 - 3);
        System.out.println("UNIT TEST: Subtraction test completed successfully!");
    }
    
    @Test
    public void multiplication_isCorrect() {
        System.out.println("UNIT TEST: Starting multiplication test");
        assertEquals("3 * 4 should equal 12", 12, 3 * 4);
        System.out.println("UNIT TEST: Multiplication test completed successfully!");
    }
    
    @Test
    public void intentionallyFailingTest() {
        System.out.println("UNIT TEST: Running test that will fail to demonstrate reporting");
        // This test will fail to show how failures appear in reports
        assertEquals("This will fail to demonstrate error reporting", 5, 3);
    }
    
    @After
    public void tearDown() {
        System.out.println("UNIT TEST: Tearing down test environment");
    }
}
