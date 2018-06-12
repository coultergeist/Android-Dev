package com.example.daniellecoulter.ad340_coulter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class TextEntryUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    //test #1
    @Test
    public void message_field_exists() throws Exception {
        //check the plain text field for entry
        onView(withId(R.id.editText))
                .check(matches(isDisplayed()));

        //verify presence of submit button
        onView(withId(R.id.button)).check(matches(isDisplayed()));

    }//end message_field_exists test #1

    //test #2
    @Test
    public void verify_text_entry() {
        // enter testing text into text field
        onView(withId(R.id.editText)).perform(typeText("Espresso testing"));

        //click button to verify intent executes
        onView(withId(R.id.button)).perform(click());

    }//end verify_text_entry test #2
}