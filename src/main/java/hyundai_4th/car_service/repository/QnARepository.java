package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.QnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnARepository extends JpaRepository<QnA, Long> {

    // Find all QnAs ordered by created date (newest first)
    List<QnA> findAllByOrderByCreatedAtDesc();

    // Find all answered QnAs
    List<QnA> findByAnsweredTrueOrderByCreatedAtDesc();

    // Find all unanswered QnAs
    List<QnA> findByAnsweredFalseOrderByCreatedAtDesc();

    // Find QnAs by author
    List<QnA> findByAuthorOrderByCreatedAtDesc(String author);
}
