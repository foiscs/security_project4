package hyundai_4th.car_service.repository;

import hyundai_4th.car_service.model.entity.User_Signup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User_Signup, UUID> {
    Optional<User_Signup> findByEmail(String email);
    boolean existsByEmail(String email);
}
