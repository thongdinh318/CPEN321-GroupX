package com.groupx.quicknews;

import com.groupx.quicknews.forums.ForumActivityTest;
import com.groupx.quicknews.search.SearchArticlesTest;
import com.groupx.quicknews.search.SearchTestSuite;
import com.groupx.quicknews.subscription.SubscriptionTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({LoginActivityTest.class, ForumActivityTest.class, SearchArticlesTest.class, SubscriptionTest.class})
public class AllTestsSuite {
}
