package hyundai_4th.car_service.model.dto;

import hyundai_4th.car_service.model.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 정보 응답 DTO
 * 결제 생성, 조회 시 반환되는 데이터
 */
public class PaymentResponse {

    private String paymentId;
    private String rentalId;
    private UserResponse user;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String status;
    private String pgTxId;
    private LocalDateTime createdAt;

    // 기본 생성자
    public PaymentResponse() {
    }

    // Entity를 DTO로 변환하는 생성자
    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.rentalId = payment.getRental().getRentalId();
        this.user = new UserResponse(payment.getUser());
        this.amount = payment.getAmount();
        this.currency = payment.getCurrency();
        this.method = payment.getMethod();
        this.status = payment.getStatus().name();
        this.pgTxId = payment.getPgTxId();
        this.createdAt = payment.getCreatedAt();
    }

    // Getter & Setter
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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