package hyundai_4th.car_service.model.dto;

public class AnswerRequest {

    private String answer;

    // Constructors
    public AnswerRequest() {}

    public AnswerRequest(String answer) {
        this.answer = answer;
    }

    // Getters and Setters
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
