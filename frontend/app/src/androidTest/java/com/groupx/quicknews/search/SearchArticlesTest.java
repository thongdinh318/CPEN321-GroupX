package com.groupx.quicknews.search;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.groupx.quicknews.util.Util.atPosition;
import static com.groupx.quicknews.util.Util.childAtPosition;
import static com.groupx.quicknews.util.Util.dateWithinRange;
import static com.groupx.quicknews.util.Util.getCurrentActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.DatePicker;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.groupx.quicknews.BaseActivity;
import com.groupx.quicknews.R;
import com.groupx.quicknews.util.ToastMatcher;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchArticlesTest {

    @Rule
    public ActivityScenarioRule<BaseActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(BaseActivity.class);

    @Test
    public void specialCharactersInSearch() {
        //wait for previous tests toast to clear
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("!@#$%^&*"), closeSoftKeyboard());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        searchButton.perform(click());

        onView(withText("Invalid query. Please try again")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void invalidDateRange() {
        //wait for previous tests toast to clear
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //set date_to to Nov 28th
        ViewInteraction toButton = onView(
                allOf(withId(R.id.date_picker_to), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                3),
                        isDisplayed()));
        toButton.perform(click());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 11, 28));

        ViewInteraction toConfirmButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        toConfirmButton.perform(scrollTo(), click());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        searchButton.perform(click());

        onView(withText("Invalid date range. Please try again")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void noArticlesFound() {
        //wait for previous tests toast to clear
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ViewInteraction searchAutoComplete = onView(
                allOf(withClassName(is("android.widget.SearchView$SearchAutoComplete")),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.LinearLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("Floccinaucinihilipilification"), closeSoftKeyboard());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        searchButton.perform(click());

        onView(withText("No articles matched")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void validSearchWithFilters() {
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.publisher_input),
                        childAtPosition(
                                allOf(withId(R.id.layout_publisher),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                2)),
                                1),
                        isDisplayed()));
        appCompatSpinner.perform(click());
        onData(allOf(is(instanceOf(String.class)), is("cbc"))).perform(click());
        appCompatSpinner.check(matches(withSpinnerText(containsString("cbc"))));

        //set date_from to Nov 28th
        ViewInteraction fromButton = onView(
                allOf(withId(R.id.date_picker_from), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                1),
                        isDisplayed()));
        fromButton.perform(click());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 11, 28));

        ViewInteraction fromConfirmButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        fromConfirmButton.perform(scrollTo(), click());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        searchButton.perform(click());


        Activity activity = getCurrentActivity();
        RecyclerView recyclerView = activity.findViewById(R.id.view_article);
        int count = recyclerView.getAdapter().getItemCount();

        Date date = new Date();
        String curDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
        for (int i = 0; i < count; i ++) {
            onView(withId(R.id.view_article))
                    .perform(scrollToPosition(i))
                    .check(matches(atPosition(i,
                            hasDescendant(allOf(withId(R.id.text_publisher), withText("cbc.ca"))))))
                    .check(matches(atPosition(i,
                            hasDescendant(allOf(withId(R.id.text_date_published), dateWithinRange("28/11/2023", curDate))))));
        }
    }

    @Test
    public void checkArticleCardView() {
        //set date_from to Nov 28th
        ViewInteraction fromButton = onView(
                allOf(withId(R.id.date_picker_from), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                1),
                        isDisplayed()));
        fromButton.perform(click());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 11, 28));

        ViewInteraction fromConfirmButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        fromConfirmButton.perform(scrollTo(), click());

        ViewInteraction searchButton = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        searchButton.perform(click());

        ViewInteraction articleView = onView(
                allOf(withId(R.id.view_article),
                        childAtPosition(
                                withClassName(is("androidx.core.widget.NestedScrollView")),
                                0)));
        articleView.perform(actionOnItemAtPosition(0, click()));
        articleView.check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.text_article_synopsis), isDisplayed())))));
        articleView.check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.button_redirect_article), isDisplayed())))));

        //check that button redirects to website
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(allOf(withId(R.id.button_redirect_article), isDisplayed())).perform(click());
        intended(expectedIntent);
        Intents.release();
    }
}
