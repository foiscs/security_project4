package hyundai_4th.car_service.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")                            // uuid 자동 생성
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_id", length = 36, nullable = false, updatable = false, columnDefinition = "char(36)")
    private String userId;                                          // API/DB 모두에서 다루기 쉬운 String 형태

    @Column(name = "email", length = 320, unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "id_type", length = 20)
    private String idType;                                          // 신분증 타입 (예: 주민등록증, 운전면허증)

    @Lob
    @Column(name = "id_number_enc")
    private byte[] idNumberEnc;                                     // 암호화된 신분증 번호

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;                                      // ISO-8601 UTC(Z) 직렬화 용이

    @Column(name = "status", length = 20, nullable = false)
    private String status = "active";                               // 기본값: active

    // 기본 생성자
    public User() {}

    // 편의 생성자
    public User(String email, String passwordHash, String name, String phone) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
    }

    // 생성 전 기본값 보강 (status만 보강)
    @PrePersist
    protected void onCreate() {
        if (this.status == null || this.status.isBlank()) {
            this.status = "active";
        }
    }

    // ----- Getter & Setter -----
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    public byte[] getIdNumberEnc() { return idNumberEnc; }
    public void setIdNumberEnc(byte[] idNumberEnc) { this.idNumberEnc = idNumberEnc; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
