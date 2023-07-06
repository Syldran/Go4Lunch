package com.ocproject7.go4lunch.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.auth.FirebaseAuth;
import com.ocproject7.go4lunch.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationActivityTestLogin {

    @Rule
    public ActivityScenarioRule<NavigationActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(NavigationActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void navigationActivityTestLogin() {

        Sleep(10000);

        onView(
                allOf(
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed())).perform(click());
        onView(withId(R.id.nav_logout)).perform(click());


        Sleep(3000);
        onView(withId(R.id.login_mail_btn)).perform(click());
        Sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100);
        Sleep(3000);
        onView(withId(com.firebase.ui.auth.R.id.email)).perform(scrollTo(), replaceText("test@mail.com"), closeSoftKeyboard());
        onView(withId(com.firebase.ui.auth.R.id.button_next)).perform(click());
        Sleep(3000);
        onView(withId(com.firebase.ui.auth.R.id.password)).perform(scrollTo(), replaceText("123456"), closeSoftKeyboard());
        Sleep(3000);
        onView(withId(com.firebase.ui.auth.R.id.button_done)).perform(click());

    }

    @Test
    public void navigationActivityTestBottomBar() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            navigationActivityTestLogin();
        }
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_list)).perform(click());
        Sleep(3000);
        onView(withId(R.id.list_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_workmates)).perform(click());
        Sleep(3000);
        onView(withId(R.id.recyclerView_workmates)).check(matches(isDisplayed()));
        onView(withId(R.id.bottom_map)).perform(click());
        Sleep(3000);
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void Sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
