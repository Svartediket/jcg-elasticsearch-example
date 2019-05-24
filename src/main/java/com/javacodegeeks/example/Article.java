package com.javacodegeeks.example;

public class Article {
    private String href;
    private String title;
    private String text;
    private String event;
    private String date;

    public Article(String href, String title, String text, String event, String date) {
        this.href = href;
        this.title = title;
        this.text = text;
        this.event = event;
        this.date = date;
    }


    public String getHref() {
        return href;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getEvent() {
        return event;
    }

    public String getDate() {
        return date;
    }
}
