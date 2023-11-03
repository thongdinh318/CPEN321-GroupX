package com.groupx.quicknews.ui.articles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    String title;
    String  url;
    String synopsis;
    String publisher;
    int articleId;
    Boolean articleRead;
    // ChatGPT usage: No.
    @JsonCreator
    public Article(
        @JsonProperty("title") String title,
        @JsonProperty("url") String url,
        @JsonProperty("content") String synopsis,
        @JsonProperty("articleId") int articleId)
    {
        this.title = title;
        this.url = url;
        this.synopsis = synopsis;
        //this.publisher = publisher;
        this.articleId = articleId;
        this.articleRead = false;
    }
    // ChatGPT usage: No.
    public String getTitle() {
        return title;
    }
    // ChatGPT usage: No.
    public void setTitle(String title) {
        this.title = title;
    }
    // ChatGPT usage: No.
    public String getUrl() {
        return url;
    }
    // ChatGPT usage: No.
    public void setUrl(String url) {
        this.url = url;
    }
    // ChatGPT usage: No.
    public String getSynopsis() {
        return synopsis;
    }
    // ChatGPT usage: No.
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    // ChatGPT usage: No.
    public String getPublisher() { return publisher; }
    // ChatGPT usage: No.
    public void setPublisher(String publisher) { this.publisher = publisher; }
    // ChatGPT usage: No.
    public Boolean getArticleRead() { return articleRead; }
    // ChatGPT usage: No.
    public void setArticleRead(Boolean articleRead) { this.articleRead = articleRead; }
    // ChatGPT usage: No.
    public int getArticleId() { return articleId; }
    // ChatGPT usage: No.
    public void setArticleId(int articleId) { this.articleId = articleId; }
}
