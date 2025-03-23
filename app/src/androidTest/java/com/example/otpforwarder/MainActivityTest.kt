package com.example.otpforwarder

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testEmailFieldValidation() {
        // Enter invalid email
        onView(withId(R.id.emailEditText))
            .perform(clearText(), typeText("invalid-email"), closeSoftKeyboard())
        
        // Click save button
        onView(withId(R.id.saveButton)).perform(click())
        
        // Check error message displayed
        onView(withId(R.id.emailEditText))
            .check(matches(hasErrorText("Invalid email address")))
        
        // Enter valid email
        onView(withId(R.id.emailEditText))
            .perform(clearText(), typeText("test@example.com"), closeSoftKeyboard())
        
        // Click save button
        onView(withId(R.id.saveButton)).perform(click())
        
        // Check success message
        onView(withId(R.id.statusTextView))
            .check(matches(withText("Settings saved successfully")))
    }
    
    @Test
    fun testServiceToggle() {
        // Check service switch exists
        onView(withId(R.id.serviceSwitch)).check(matches(isDisplayed()))
        
        // Toggle service on
        onView(withId(R.id.serviceSwitch)).perform(click())
        
        // Check status text indicates service is running
        onView(withId(R.id.statusTextView))
            .check(matches(withText(containsString("Service is running"))))
        
        // Toggle service off
        onView(withId(R.id.serviceSwitch)).perform(click())
        
        // Check status text indicates service is stopped
        onView(withId(R.id.statusTextView))
            .check(matches(withText(containsString("Service is stopped"))))
    }
    
    @Test
    fun testPermissionRequestFlow() {
        // Assuming there's a button to request permissions
        onView(withId(R.id.requestPermissionsButton)).perform(click())
        
        // Cannot directly test system dialogs with Espresso, but we can check that
        // the app shows appropriate UI elements based on permission state
        // For example, after clicking permission button, check if status message updates
        onView(withId(R.id.permissionStatusText))
            .check(matches(isDisplayed()))
    }
}
