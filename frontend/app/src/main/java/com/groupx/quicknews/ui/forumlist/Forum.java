package com.groupx.quicknews.ui.forumlist;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Forum {
    String forumID;
    String forumName;
    @JsonCreator
    public Forum(@JsonProperty("_id") String forumID, @JsonProperty("name") String forumName) {
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
