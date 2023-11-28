package com.groupx.quicknews.forums;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.groupx.quicknews.ForumsListActivity;
import com.groupx.quicknews.R;
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
public class ForumsListActivityTest {

    private RecyclerViewIdlingResource idlingResource;
    @Rule
    public ActivityScenarioRule<ForumsListActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(ForumsListActivity.class);

    @Before
    public void setUp() {
        final RecyclerView[] recyclerView = new RecyclerView[1];
        mActivityScenarioRule.getScenario().onActivity(activity -> {
            recyclerView[0] = activity.findViewById(R.id.view_forum);
        });
        idlingResource = new RecyclerViewIdlingResource(recyclerView[0]);
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void tearDown() {
        // Unregister the RecyclerViewIdlingResource after the test
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
    @Test
    public void forumListLoadedTest() {
        ViewInteraction recyclerView = onView(withId(R.id.view_forum));
        recyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        recyclerView.check(matches(RecyclerViewMatchers.withItemCountGreaterThan(0)));
    }

    @Test
    public void navForumTest() {
        ViewInteraction forumRecyclerView = onView(withId(R.id.view_forum));
        forumRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        forumRecyclerView.check(matches(RecyclerViewMatchers.withItemCountGreaterThan(0)));
        forumRecyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction commentRecyclerView = onView(withId(R.id.view_comment));
        commentRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_post), withHint("Enter Message"),
                        withParent(allOf(withId(R.id.layout_make_post))),
                        isDisplayed()));
        editText.check(matches(withHint("Enter Message")));

        ViewInteraction button = onView(
                allOf(withId(R.id.button_post), withText("Post"),
                        withParent(allOf(withId(R.id.layout_make_post))),
                        isDisplayed()));
        button.check(matches(isNotEnabled()));
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
