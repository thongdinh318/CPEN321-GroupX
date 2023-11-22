package com.groupx.quicknews;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.groupx.quicknews.util.RecyclerViewIdlingResource;
import com.groupx.quicknews.util.RecyclerViewMatchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ForumActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void openForumListTest() {

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_forums), withContentDescription("Forums"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction recyclerView = onView(withId(R.id.view_forum));
        recyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))); // Check if the RecyclerView is visible
        }

    @Rule
    public ActivityScenarioRule<ForumsListActivity> mForumsActivityRule =
            new ActivityScenarioRule<>(ForumsListActivity.class);
    private RecyclerViewIdlingResource idlingResource;
    @Before
    public void setUp() {
        // Register the RecyclerViewIdlingResource before the test
        final RecyclerView[] recyclerView = new RecyclerView[1];
        mActivityScenarioRule.getScenario().onActivity(activity -> {
            recyclerView[0] = activity.findViewById(R.id.view_forum);
        });
        idlingResource = new RecyclerViewIdlingResource(recyclerView[0]);
        IdlingRegistry.getInstance().register(idlingResource);
    }
    @Test
    public void forumListLoadedTest() {
        ViewInteraction recyclerView = onView(withId(R.id.view_forum));
        recyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        recyclerView.check(matches(RecyclerViewMatchers.withItemCountGreaterThan(0)));

        recyclerView.perform(actionOnItemAtPosition(0, click()));
    }
    @After
    public void tearDown() {
        // Unregister the RecyclerViewIdlingResource after the test
        IdlingRegistry.getInstance().unregister(idlingResource);
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
}
