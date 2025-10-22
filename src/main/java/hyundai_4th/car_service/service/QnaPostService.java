package hyundai_4th.car_service.service;

import hyundai_4th.car_service.model.dto.QnaPostRequest;
import hyundai_4th.car_service.model.dto.QnaPostResponse;
import hyundai_4th.car_service.model.entity.QnaPost;
import hyundai_4th.car_service.repository.QnaPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QnaPostService {

    @Autowired
    private QnaPostRepository qnaPostRepository;

    // 전체 조회
    public List<QnaPostResponse> getAllPosts() {
        return qnaPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(QnaPostResponse::new)
                .collect(Collectors.toList());
    }

    // ID로 조회
    public QnaPostResponse getPostById(Integer id) {
        QnaPost post = qnaPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnaPost not found with id: " + id));
        return new QnaPostResponse(post);
    }

    // 상태별 조회
    public List<QnaPostResponse> getPostsByStatus(QnaPost.QnaStatus status) {
        return qnaPostRepository.findByStatus(status)
                .stream()
                .map(QnaPostResponse::new)
                .collect(Collectors.toList());
    }

    // 새 글 작성
    @Transactional
    public QnaPostResponse createPost(QnaPostRequest request) {
        QnaPost post = new QnaPost();
        post.setAuthor(request.getAuthor());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        QnaPost savedPost = qnaPostRepository.save(post);
        return new QnaPostResponse(savedPost);
    }

    // 수정
    @Transactional
    public QnaPostResponse updatePost(Integer id, QnaPostRequest request) {
        QnaPost post = qnaPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnaPost not found with id: " + id));

        post.setAuthor(request.getAuthor());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        QnaPost updatedPost = qnaPostRepository.save(post);
        return new QnaPostResponse(updatedPost);
    }

    // 상태 변경 (답변완료 처리)
    @Transactional
    public QnaPostResponse updateStatus(Integer id, QnaPost.QnaStatus status) {
        QnaPost post = qnaPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QnaPost not found with id: " + id));

        post.setStatus(status);

        QnaPost updatedPost = qnaPostRepository.save(post);
        return new QnaPostResponse(updatedPost);
    }

    // 삭제
    @Transactional
    public void deletePost(Integer id) {
        if (!qnaPostRepository.existsById(id)) {
            throw new RuntimeException("QnaPost not found with id: " + id);
        }
        qnaPostRepository.deleteById(id);
    }
}