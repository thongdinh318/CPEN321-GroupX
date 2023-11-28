package com.groupx.quicknews.forums;


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
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import static kotlinx.coroutines.flow.FlowKt.withIndex;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.groupx.quicknews.ForumActivity;
import com.groupx.quicknews.ForumsListActivity;
import com.groupx.quicknews.LoginActivity;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ForumActivityTest {

    private RecyclerViewIdlingResource idlingResource;
    private RecyclerView recyclerViewComments;
    @Rule
    public ActivityScenarioRule<ForumsListActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(ForumsListActivity.class);

    @Before
    public void setUp() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.view_forum),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                1)));
        recyclerView.perform(actionOnItemAtPosition(2, click()));
    }

        @Test
    public void forumListLoadedTest() {
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

    @Test
    public void postSingleCommentTest() {
        ViewInteraction commentRecyclerView = onView(withId(R.id.view_comment));
        commentRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        ViewInteraction button = onView(
                allOf(withId(R.id.button_post), withText("Post"),
                        withParent(withId(R.id.layout_make_post)),
                        isDisplayed()));
        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_post), withHint("Enter Message"),
                        withParent(withId(R.id.layout_make_post)),
                        isDisplayed()));
        try {
        editText.perform(replaceText("Test Post 4"), closeSoftKeyboard());
        button.check(matches(isEnabled()));
        button.perform(click());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ViewInteraction comment = onView(
                allOf(withId(R.id.text_comment), withText("Test Post 4") ,
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        comment.check(matches(withText("Test Post 4")));

        ViewInteraction user = onView(
                allOf(withId(R.id.text_user), withText("Ryan Clayton") ,
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        user.check(matches(withText("Ryan Clayton")));

        editText.check(matches(withHint("Enter Message")));
        button.check(matches(isNotEnabled()));
    }

    @Test
    public void postMultipleCommentsTest() {
        ViewInteraction commentRecyclerView = onView(withId(R.id.view_comment));
        commentRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        ViewInteraction button = onView(
                allOf(withId(R.id.button_post), withText("Post"),
                        withParent(withId(R.id.layout_make_post)),
                        isDisplayed()));
        ViewInteraction editText = onView(
                allOf(withId(R.id.edit_post), withHint("Enter Message"),
                        withParent(withId(R.id.layout_make_post)),
                        isDisplayed()));
        try {
        editText.perform(replaceText("Test Post 2"), closeSoftKeyboard());
        button.perform(click());
        Thread.sleep(2000);

        editText.perform(replaceText("Test Post 3"), closeSoftKeyboard());
        button.perform(click());
        Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        ViewInteraction comment = onView(
                allOf(withId(R.id.text_comment), withText("Test Post 2") ,
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class))),
                        isDisplayed()));
        comment.check(matches(withText("Test Post 2")));

        comment = onView(
                allOf(withId(R.id.text_comment), withText("Test Post 3") ,
                        withParent(withId(R.id.view_comment)),
                        isDisplayed()));
        comment.check(matches(withText("Test Post 3")));

        editText.check(matches(withHint("Enter Message")));
        button.check(matches(isNotEnabled()));
    }

    @Test
    public void receiveNewCommentTest() {
        ViewInteraction commentRecyclerView = onView(withId(R.id.view_comment));
        commentRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ViewInteraction comment = onView(
                allOf(withId(R.id.text_comment), withText("Received Test Post 1") ,
                        withParent(withId(R.id.view_comment)),
                        isDisplayed()));
        comment.check(matches(withText("Received Test Post 1")));
    }


    //https://stackoverflow.com/questions/55653555/androidespresso-how-get-item-on-specific-position-of-recyclerview/55657804#55657804
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
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