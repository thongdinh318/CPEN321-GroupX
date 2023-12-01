package com.groupx.quicknews.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

public class RecyclerViewIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private RecyclerView recyclerView;

    public RecyclerViewIdlingResource(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public String getName() {
        return RecyclerViewIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = recyclerView != null && recyclerView.getAdapter() != null;
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}