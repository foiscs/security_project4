package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 이메일로 사용자 조회 (로그인, 회원가입 중복 체크)
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 전화번호로 사용자 조회
    Optional<User> findByPhone(String phone);

    // 상태로 사용자 목록 조회
    List<User> findByStatus(String status);

    // 이름으로 사용자 검색
    List<User> findByNameContaining(String name);
}
