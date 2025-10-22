package hyundai_4th.car_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import hyundai_4th.car_service.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
