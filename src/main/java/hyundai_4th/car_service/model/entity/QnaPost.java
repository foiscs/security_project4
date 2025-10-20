package hyundai_4th.car_service.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qnapost")
public class QnaPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition = "VARCHAR(36)")
    private User user;  // 작성자 (User 엔티티 참조)

    @Column(name = "author", length = 100)
    private String author;  // 작성자 이름 (임시 호환성 유지)

    @Column(name = "title", length = 200, nullable = false)
    private String title;  // 제목

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;  // 내용

    @Column(name = "category", length = 100)
    private String category;  // 카테고리 (기본값: '')

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QnaStatus status = QnaStatus.접수됨;  // 기본값: 접수됨

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // QnA 상태 Enum
    public enum QnaStatus {
        접수됨,
        답변완료
    }

    // JPA 자동 시간 설정
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (category == null) {
            category = "";
        }
        if (status == null) {
            status = QnaStatus.접수됨;
        }
    }

    // 기본 생성자
    public QnaPost() {
    }

    // 생성자
    public QnaPost(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    // Getter & Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QnaStatus getStatus() {
        return status;
    }

    public void setStatus(QnaStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
