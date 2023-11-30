package com.groupx.quicknews.forums;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
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
import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.groupx.quicknews.BaseActivity;
import com.groupx.quicknews.ForumActivity;
import com.groupx.quicknews.ForumsListFragment;
import com.groupx.quicknews.R;
import com.groupx.quicknews.util.RecyclerViewIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Objects;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ForumActivityTest {

    private RecyclerViewIdlingResource idlingResource;
    private RecyclerView recyclerViewComments;
    @Rule
    public ActivityScenarioRule<BaseActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(BaseActivity.class);

    @Before
    public void setUp() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_forums), withContentDescription("Forums"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.view_forum),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
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
        editText.perform(replaceText("Test Post 1"), closeSoftKeyboard());
        button.check(matches(isEnabled()));
        button.perform(click());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Activity activity = getCurrentActivity();
        RecyclerView recyclerView = activity.findViewById(R.id.view_comment);
        int count = recyclerView.getAdapter().getItemCount();

        onView(withId(R.id.view_comment))
                .perform(scrollToPosition(count - 1))
                .check(matches(atPosition(count - 1, hasDescendant(
                        allOf(withId(R.id.text_comment), withText("Test Post 1"))))));

        onView(withId(R.id.view_comment))
                .check(matches(atPosition(count - 1, hasDescendant(
                        allOf(withId(R.id.text_user), withText("Ryan Clayton"))))));

        editText.check(matches(withHint("Enter Message")));
        button.check(matches(isNotEnabled()));
    }

    //@Test
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

    //@Test
    public void receiveNewCommentTest() {
        ViewInteraction commentRecyclerView = onView(withId(R.id.view_comment));
        commentRecyclerView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ViewInteraction comment = onView(
                allOf(withId(R.id.text_comment), withText("Received Test Post 1") ,
                        withParent(withId(R.id.view_comment)),
                        isDisplayed()));
        comment.check(matches(withText("Received Test Post 1")));
    }


    public Activity getCurrentActivity() {
        final Activity[] currentActivity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                Collection<Activity> allActivities = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(Stage.RESUMED);
                if (!allActivities.isEmpty()) {
                    currentActivity[0] = allActivities.iterator().next();
                }
            }
        });
        return currentActivity[0];
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

    //https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}