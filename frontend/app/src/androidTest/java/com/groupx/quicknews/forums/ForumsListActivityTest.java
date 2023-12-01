package com.groupx.quicknews.forums;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.groupx.quicknews.R;
import com.groupx.quicknews.util.RecyclerViewMatchers;

import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ForumsListActivityTest {

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
}
