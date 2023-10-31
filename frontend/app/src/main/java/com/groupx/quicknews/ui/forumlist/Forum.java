package com.groupx.quicknews.ui.forumlist;

public class Forum {
    String forumID;
    String forumName;
    public Forum(String forumID, String forumName) {
        this.forumID = forumID;
        this.forumName = forumName;
    }

    public String getForumID() {
        return forumID;
    }

    public void setForumID(String forumID) {
        this.forumID = forumID;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }




}
