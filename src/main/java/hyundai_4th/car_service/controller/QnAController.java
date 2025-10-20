package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.AnswerRequest;
import hyundai_4th.car_service.model.dto.QnARequest;
import hyundai_4th.car_service.model.dto.QnAResponse;
import hyundai_4th.car_service.service.QnAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@CrossOrigin(origins = "*")
public class QnAController {

    @Autowired
    private QnAService qnaService;

    // Get all QnAs
    @GetMapping
    public ResponseEntity<List<QnAResponse>> getAllQnAs() {
        List<QnAResponse> qnas = qnaService.getAllQnAs();
        return ResponseEntity.ok(qnas);
    }

    // Get QnA by ID
    @GetMapping("/{id}")
    public ResponseEntity<QnAResponse> getQnAById(@PathVariable Long id) {
        try {
            QnAResponse qna = qnaService.getQnAById(id);
            return ResponseEntity.ok(qna);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get answered QnAs
    @GetMapping("/answered")
    public ResponseEntity<List<QnAResponse>> getAnsweredQnAs() {
        List<QnAResponse> qnas = qnaService.getAnsweredQnAs();
        return ResponseEntity.ok(qnas);
    }

    // Get unanswered QnAs
    @GetMapping("/unanswered")
    public ResponseEntity<List<QnAResponse>> getUnansweredQnAs() {
        List<QnAResponse> qnas = qnaService.getUnansweredQnAs();
        return ResponseEntity.ok(qnas);
    }

    // Create new QnA
    @PostMapping
    public ResponseEntity<QnAResponse> createQnA(@RequestBody QnARequest request) {
        try {
            QnAResponse qna = qnaService.createQnA(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(qna);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Update QnA
    @PutMapping("/{id}")
    public ResponseEntity<QnAResponse> updateQnA(@PathVariable Long id, @RequestBody QnARequest request) {
        try {
            QnAResponse qna = qnaService.updateQnA(id, request);
            return ResponseEntity.ok(qna);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Add answer to QnA
    @PostMapping("/{id}/answer")
    public ResponseEntity<QnAResponse> addAnswer(@PathVariable Long id, @RequestBody AnswerRequest request) {
        try {
            QnAResponse qna = qnaService.addAnswer(id, request);
            return ResponseEntity.ok(qna);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete QnA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQnA(@PathVariable Long id) {
        try {
            qnaService.deleteQnA(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
