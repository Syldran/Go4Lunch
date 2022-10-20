package com.ocproject7.go4lunch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.ViewInteraction.*;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

import com.ocproject7.go4lunch.ui.NavigationActivity;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<NavigationActivity> activityScenarioRule =
            new ActivityScenarioRule<>(NavigationActivity.class);


    @Before
    public void setUp() {
        ActivityScenario scenario = activityScenarioRule.getScenario();
        assertThat(scenario, notNullValue());
    }
/*
    @Test
    public void meetingListNotEmpty_and_RoomsCountValue() {
        //Check there is at least one item in recyclerview
        onView(ViewMatchers.withId(R.id.main_meeting_recyclerview)).check(matches(hasMinimumChildCount(1)));

        //Open Add meeting activity to check number of rooms
        onView(ViewMatchers.withId(R.id.main_add_meeting)).perform(click());
        assertThat(ViewMatchers.withId(R.layout.activity_add_meeting), notNullValue());

        //set Date
        onView(ViewMatchers.withId(R.id.add_meeting_edit_text_date)).perform(click());
        onView(withId(R.id.confirm_button)).perform(click());

        //set Start
        onView(ViewMatchers.withId(R.id.add_meeting_edit_text_time_start)).perform(click());
        onView(withId(R.id.material_timepicker_mode_button)).perform(click());
        onView(ViewMatchers.withId(R.id.material_hour_text_input)).perform(click());
        onView(ViewMatchers.isFocused()).perform(typeText("10"));
        onView(withId(R.id.material_timepicker_ok_button)).perform(click());

        //set End
        onView(withId(R.id.add_meeting_edit_text_time_end)).perform(click());
        onView(withId(R.id.material_timepicker_mode_button)).perform(click());
        onView(ViewMatchers.withId(R.id.material_hour_text_input)).perform(click());
        onView(ViewMatchers.isFocused()).perform(typeText("11"));
        onView(withId(R.id.material_timepicker_ok_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));

        //Check our spinner items count
        onView(withId(R.id.spinnerRooms)).check(SpinnerItemCountAssertion.withItemCount(ROOMS_COUNT));
    }

    @Test
    public void myMeetingList_delete_and_add_with_success() {
        onView(ViewMatchers.withId(R.id.main_meeting_recyclerview)).check(withItemCount(ITEMS_COUNT));
        onView(ViewMatchers.withId(R.id.main_meeting_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(1, new DeleteViewAction()));
        onView(ViewMatchers.withId(R.id.main_meeting_recyclerview)).check(withItemCount(ITEMS_COUNT - 1));
        ITEMS_COUNT -= 1;
    }
*/
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ocproject7.go4lunch", appContext.getPackageName());
    }

    @Test
    public void fragmentOnButton(){
        onView(ViewMatchers.withId(R.id.bottom_map).perform(click()));
        //onView(ViewMatchers.withId(R.id.nav_host_fragment_content_main).matches())
    }


}