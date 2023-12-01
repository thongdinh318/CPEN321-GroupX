package com.groupx.quicknews.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class RecyclerViewMatchers {
    public static Matcher<Object> withItemCountGreaterThan(final int count) {
        return new BoundedMatcher<Object, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with item count greater than: " + count);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                return adapter != null && adapter.getItemCount() > count;
            }
        };
    }

    public static Matcher<Object> withItemCountEqual(final int count) {
        return new BoundedMatcher<Object, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with item count equal to: " + count);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                return adapter != null && adapter.getItemCount() == count;
            }
        };
    }
}