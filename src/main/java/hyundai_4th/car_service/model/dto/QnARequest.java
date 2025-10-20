package hyundai_4th.car_service.model.dto;

public class QnARequest {

    private String title;
    private String question;
    private String author;

    // Constructors
    public QnARequest() {}

    public QnARequest(String title, String question, String author) {
        this.title = title;
        this.question = question;
        this.author = author;
    }

    // Getters and Setters
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
