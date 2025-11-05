package com.security.test.controller;

import com.security.test.model.dto.QnaPostRequest;
import com.security.test.model.dto.QnaPostResponse;
import com.security.test.model.entity.QnaPost;
import com.security.test.service.QnaPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@CrossOrigin(origins = "*")
public class QnaPostController {

    @Autowired
    private QnaPostService qnaPostService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<QnaPostResponse>> getAllPosts() {
        List<QnaPostResponse> posts = qnaPostService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<QnaPostResponse> getPostById(@PathVariable Integer id) {
        try {
            QnaPostResponse post = qnaPostService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 답변 완료된 글 조회
    @GetMapping("/answered")
    public ResponseEntity<List<QnaPostResponse>> getAnsweredPosts() {
        List<QnaPostResponse> posts = qnaPostService.getPostsByStatus(QnaPost.QnaStatus.답변완료);
        return ResponseEntity.ok(posts);
    }

    // 접수된 글 조회 (답변 대기)
    @GetMapping("/unanswered")
    public ResponseEntity<List<QnaPostResponse>> getUnansweredPosts() {
        List<QnaPostResponse> posts = qnaPostService.getPostsByStatus(QnaPost.QnaStatus.접수됨);
        return ResponseEntity.ok(posts);
    }

    // 새 글 작성
    @PostMapping
    public ResponseEntity<QnaPostResponse> createPost(@RequestBody QnaPostRequest request) {
        try {
            QnaPostResponse post = qnaPostService.createPost(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<QnaPostResponse> updatePost(@PathVariable Integer id, @RequestBody QnaPostRequest request) {
        try {
            QnaPostResponse post = qnaPostService.updatePost(id, request);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 답변완료 처리
    @PostMapping("/{id}/answer")
    public ResponseEntity<QnaPostResponse> markAsAnswered(@PathVariable Integer id) {
        try {
            QnaPostResponse post = qnaPostService.updateStatus(id, QnaPost.QnaStatus.답변완료);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        try {
            qnaPostService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
