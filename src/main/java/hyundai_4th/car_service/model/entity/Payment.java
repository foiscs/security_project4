package hyundai_4th.car_service.model.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments",
       indexes = {
           @Index(name = "idx_pay_rental_time", columnList = "rental_id, created_at"),
           @Index(name = "ux_pay_pg_tx_id", columnList = "pg_tx_id", unique = true)
       })
public class Payment {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "payment_id", length = 36, nullable = false)
    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;  // 대여 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 사용자

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;  // 결제 금액

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;  // 통화 (예: "KRW", "USD")

    @Column(name = "method", length = 30, nullable = false)
    private String method;  // 결제 수단 (예: "card", "cash", "transfer")

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.AUTHORIZED;

    @Column(name = "pg_tx_id", length = 128)
    private String pgTxId;  // PG사 거래 ID

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 결제 상태 Enum
    public enum PaymentStatus {
        AUTHORIZED,  // 승인됨
        CAPTURED,    // 결제 완료
        VOIDED,      // 취소됨
        REFUNDED,    // 환불됨
        FAILED       // 실패
    }

    // JPA 자동 시간 설정
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.AUTHORIZED;
        }
    }

    // 기본 생성자
    public Payment() {
    }

    // 생성자
    public Payment(Rental rental, User user, BigDecimal amount, String currency, String method) {
        this.rental = rental;
        this.user = user;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
    }

    // Getter & Setter
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPgTxId() {
        return pgTxId;
    }

    public void setPgTxId(String pgTxId) {
        this.pgTxId = pgTxId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
