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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Boolean getArticleRead() { return articleRead; }

    public void setArticleRead(Boolean articleRead) { this.articleRead = articleRead; }

    public int getArticleId() { return articleId; }
    public void setArticleId(int articleId) { this.articleId = articleId; }
}
