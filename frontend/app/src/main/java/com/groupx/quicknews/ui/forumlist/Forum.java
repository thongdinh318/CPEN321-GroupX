package com.groupx.quicknews.ui.forumlist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forum {
    String forumID;
    String forumName;
    // ChatGPT usage: No.
    @JsonCreator
    public Forum(@JsonProperty("id") String forumID, @JsonProperty("name") String forumName) {
        this.forumID = forumID;
        this.forumName = forumName;
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
