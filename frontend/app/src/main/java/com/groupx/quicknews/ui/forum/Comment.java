package com.groupx.quicknews.ui.forum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    String user;
    String comment;
    // ChatGPT usage: No.
    @JsonCreator
    public Comment(@JsonProperty("username") String user, @JsonProperty("content") String comment) {
        this.user = user;
        this.comment = comment;
    }
    // ChatGPT usage: No.
    public String getUser() {
        return user;
    }
    // ChatGPT usage: No.
    public void setUser(String user) {
        this.user = user;
    }
    // ChatGPT usage: No.
    public String getComment() {
        return comment;
    }
    // ChatGPT usage: No.
    public void setComment(String comment) {
        this.comment = comment;
    }

}
