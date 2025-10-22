package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.QnaPost;
import hyundai_4th.car_service.model.entity.QnaPost.QnaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaPostRepository extends JpaRepository<QnaPost, Integer> {

    // 특정 사용자의 QnA 게시글 조회
    List<QnaPost> findByUser_UserId(String userId);

    // 상태로 QnA 게시글 조회
    List<QnaPost> findByStatus(QnaStatus status);

    // 카테고리로 QnA 게시글 조회
    List<QnaPost> findByCategory(String category);

    // 제목으로 검색
    List<QnaPost> findByTitleContaining(String keyword);

    // 내용으로 검색
    List<QnaPost> findByContentContaining(String keyword);

    // 제목 또는 내용으로 검색
    List<QnaPost> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

    // 특정 사용자의 특정 상태 게시글 조회
    List<QnaPost> findByUser_UserIdAndStatus(String userId, QnaStatus status);

    // 최신 순으로 정렬된 게시글 조회
    List<QnaPost> findAllByOrderByCreatedAtDesc();
}