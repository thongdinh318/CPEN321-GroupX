package com.groupx.quicknews.ui.forumlist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groupx.quicknews.ui.forum.Comment;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forum {
    String forumID;
    String forumName;
    String numComments;
    // ChatGPT usage: No.
    @JsonCreator
    public Forum(
            @JsonProperty("id") String forumID,
            @JsonProperty("name") String forumName,
            @JsonProperty("comments")Comment[] comments) {
        this.forumID = forumID;
        this.forumName = forumName;
        this.numComments = String.valueOf(comments.length);
    }

    // ChatGPT usage: No.
    public String getNumComments() {
        return numComments;
    }
    // ChatGPT usage: No.
    public String getForumID() {
        return forumID;
    }
    // ChatGPT usage: No.
    public void setForumID(String forumID) {
        this.forumID = forumID;
    }
    // ChatGPT usage: No.
    public String getForumName() {
        return forumName;
    }
    // ChatGPT usage: No.
    public void setForumName(String forumName) {
        this.forumName = forumName;
    }
}
