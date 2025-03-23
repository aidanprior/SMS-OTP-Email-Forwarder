package com.example.otpforwarder;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    
    // Add a basic context test that will always run
    @Test
    public void useAppContext() {
        // Context of the app under test
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        System.out.println("INSTRUMENTATION TEST: Testing package name");
        assertEquals("com.example.otpforwarder.debug", appContext.getPackageName());
        System.out.println("INSTRUMENTATION TEST: Package name verified successfully");
    }
    
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void simpleTest() {
        System.out.println("INSTRUMENTATION TEST: Running simple test");
        assertTrue("This simple test should always pass", true);
        System.out.println("INSTRUMENTATION TEST: Simple test passed");
    }
}