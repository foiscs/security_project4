package com.security.test.model.dto;

public class QnaPostRequest {

    private String author;
    private String title;
    private String content;
    private String category;

    // Constructors
    public QnaPostRequest() {}

    public QnaPostRequest(String author, String title, String content, String category) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // Getters and Setters
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}