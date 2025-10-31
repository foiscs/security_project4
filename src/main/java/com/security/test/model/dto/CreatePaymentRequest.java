package com.security.test.model.dto;

import java.math.BigDecimal;

/**
 * 결제 생성 요청 DTO
 * 클라이언트로부터 받는 결제 요청 데이터
 */
public class CreatePaymentRequest {

    private String rentalId;          // 대여 ID
    private BigDecimal amount;        // 결제 금액
    private String currency;          // 통화 (기본값: KRW)
    private String paymentMethod;     // 결제 수단 (card, transfer 등)
    private String customerName;      // 고객명
    private String customerEmail;     // 고객 이메일

    public CreatePaymentRequest() {
        this.currency = "KRW";
        this.paymentMethod = "card";
    }

    public CreatePaymentRequest(String rentalId, BigDecimal amount, String currency,
                               String paymentMethod, String customerName, String customerEmail) {
        this.rentalId = rentalId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    // Getters and Setters
    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
