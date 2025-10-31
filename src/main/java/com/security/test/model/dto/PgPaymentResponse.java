package com.security.test.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PG사 결제 승인 응답 DTO
 * 외부 PG사 API로부터 받는 응답 데이터
 */
public class PgPaymentResponse {

    private String resultCode;          // 결과 코드 (0000: 성공)
    private String resultMessage;       // 결과 메시지
    private String transactionId;       // PG사 거래 ID
    private String orderId;             // 주문 ID
    private BigDecimal amount;          // 승인 금액
    private String currency;            // 통화
    private String paymentMethod;       // 결제 수단
    private String status;              // 결제 상태 (APPROVED, FAILED 등)
    private LocalDateTime approvedAt;   // 승인 시각
    private String cardCompany;         // 카드사
    private String cardNumber;          // 마스킹된 카드번호
    private String installment;         // 할부 개월 (00: 일시불)

    public PgPaymentResponse() {}

    public PgPaymentResponse(String resultCode, String resultMessage, String transactionId,
                            String orderId, BigDecimal amount, String currency,
                            String paymentMethod, String status, LocalDateTime approvedAt) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.approvedAt = approvedAt;
    }

    // Getters and Setters
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }

    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getCardCompany() { return cardCompany; }
    public void setCardCompany(String cardCompany) { this.cardCompany = cardCompany; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getInstallment() { return installment; }
    public void setInstallment(String installment) { this.installment = installment; }
}
