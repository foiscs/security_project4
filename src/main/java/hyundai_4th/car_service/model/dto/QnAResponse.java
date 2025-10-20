package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.QnA;
import java.time.LocalDateTime;

public class QnAResponse {

    private Long id;
    private String title;
    private String question;
    private String answer;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean answered;

    // Constructors
    public QnAResponse() {}

    public QnAResponse(QnA qna) {
        this.id = qna.getId();
        this.title = qna.getTitle();
        this.question = qna.getQuestion();
        this.answer = qna.getAnswer();
        this.author = qna.getAuthor();
        this.createdAt = qna.getCreatedAt();
        this.updatedAt = qna.getUpdatedAt();
        this.answered = qna.isAnswered();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
