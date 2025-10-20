package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.AnswerRequest;
import hyundai_4th.car_service.model.dto.QnARequest;
import hyundai_4th.car_service.model.dto.QnAResponse;
import hyundai_4th.car_service.model.entity.QnA;
import hyundai_4th.car_service.repository.QnARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QnAService {

    @Autowired
    private QnARepository qnaRepository;

    // Get all QnAs
    public List<QnAResponse> getAllQnAs() {
        return qnaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(QnAResponse::new)
                .collect(Collectors.toList());
    }

    // Get QnA by ID
    public QnAResponse getQnAById(Long id) {
        QnA qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA not found with id: " + id));
        return new QnAResponse(qna);
    }

    // Get answered QnAs
    public List<QnAResponse> getAnsweredQnAs() {
        return qnaRepository.findByAnsweredTrueOrderByCreatedAtDesc()
                .stream()
                .map(QnAResponse::new)
                .collect(Collectors.toList());
    }

    // Get unanswered QnAs
    public List<QnAResponse> getUnansweredQnAs() {
        return qnaRepository.findByAnsweredFalseOrderByCreatedAtDesc()
                .stream()
                .map(QnAResponse::new)
                .collect(Collectors.toList());
    }

    // Create new QnA
    @Transactional
    public QnAResponse createQnA(QnARequest request) {
        QnA qna = new QnA(request.getTitle(), request.getQuestion(), request.getAuthor());
        QnA savedQnA = qnaRepository.save(qna);
        return new QnAResponse(savedQnA);
    }

    // Update QnA
    @Transactional
    public QnAResponse updateQnA(Long id, QnARequest request) {
        QnA qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA not found with id: " + id));

        qna.setTitle(request.getTitle());
        qna.setQuestion(request.getQuestion());
        qna.setAuthor(request.getAuthor());

        QnA updatedQnA = qnaRepository.save(qna);
        return new QnAResponse(updatedQnA);
    }

    // Add answer to QnA
    @Transactional
    public QnAResponse addAnswer(Long id, AnswerRequest request) {
        QnA qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnA not found with id: " + id));

        qna.setAnswer(request.getAnswer());

        QnA updatedQnA = qnaRepository.save(qna);
        return new QnAResponse(updatedQnA);
    }

    // Delete QnA
    @Transactional
    public void deleteQnA(Long id) {
        if (!qnaRepository.existsById(id)) {
            throw new RuntimeException("QnA not found with id: " + id);
        }
        qnaRepository.deleteById(id);
    }
}
