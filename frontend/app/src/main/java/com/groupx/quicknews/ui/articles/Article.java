package com.groupx.quicknews.ui.articles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {
    String title;
    String  url;
    String synopsis;
    String publisher;
    int articleId;
    Boolean articleRead;
    public Article(String title, String url, String synopsis, int articleId) {
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
