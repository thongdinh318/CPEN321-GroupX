package com.groupx.quicknews.search;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.groupx.quicknews.util.Util.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.widget.DatePicker;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.groupx.quicknews.BaseActivity;
import com.groupx.quicknews.LoginActivity;
import com.groupx.quicknews.R;
import com.groupx.quicknews.util.ToastMatcher;

import org.hamcrest.Matchers;
import org.jsoup.Connection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchArticlesTest {

    @Rule
    public ActivityScenarioRule<BaseActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(BaseActivity.class);

    //@Test
    public void specialCharactersInSearch() {
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

    //@Test
    public void invalidDateRange() {
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
    //@Test
    public void loginActivityTest2() {

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

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.date_picker_from), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                1),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button2), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        materialButton3.perform(scrollTo(), click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.date_picker_to), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                3),
                        isDisplayed()));
        materialButton4.perform(click());

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction materialButton6 = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        materialButton6.perform(click());

        ViewInteraction materialButton7 = onView(
                allOf(withId(R.id.date_picker_to), withText("NOV 23 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                3),
                        isDisplayed()));
        materialButton7.perform(click());

        ViewInteraction materialButton8 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton8.perform(scrollTo(), click());

        ViewInteraction materialButton9 = onView(
                allOf(withId(R.id.date_picker_from), withText("NOV 30 2023"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        4),
                                1),
                        isDisplayed()));
        materialButton9.perform(click());

        ViewInteraction materialButton10 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        materialButton10.perform(scrollTo(), click());

        ViewInteraction materialButton11 = onView(
                allOf(withId(R.id.filter_search_button), withText("Search"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_fragment_frame),
                                        0),
                                5),
                        isDisplayed()));
        materialButton11.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.view_article),
                        childAtPosition(
                                withClassName(is("androidx.core.widget.NestedScrollView")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
    }
}
