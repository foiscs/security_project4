package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.QnaPost;
import java.time.LocalDateTime;

public class QnaPostResponse {

    private Integer id;
    private String author;
    private String title;
    private String content;
    private String category;
    private String status;
    private LocalDateTime createdAt;

    // Constructors
    public QnaPostResponse() {}

    public QnaPostResponse(QnaPost qnaPost) {
        this.id = qnaPost.getId();
        this.author = qnaPost.getAuthor() != null ? qnaPost.getAuthor() : "익명";
        this.title = qnaPost.getTitle();
        this.content = qnaPost.getContent();
        this.category = qnaPost.getCategory();
        this.status = qnaPost.getStatus() != null ? qnaPost.getStatus().name() : "접수됨";
        this.createdAt = qnaPost.getCreatedAt();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}