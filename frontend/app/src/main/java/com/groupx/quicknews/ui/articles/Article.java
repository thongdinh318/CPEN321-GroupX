package com.groupx.quicknews.ui.articles;

public class Article {
    String title;
    String  url;
    String synopsis;
    public Article(String title, String url, String synopsis) {
        this.title = title;
        this.url = url;
        this.synopsis = synopsis;
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
}
