import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testButtonClick() {
        Espresso.onView(ViewMatchers.withId(R.id.button))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.textView))
                .check(ViewAssertions.matches(ViewMatchers.withText("Button Clicked")));
    }
}