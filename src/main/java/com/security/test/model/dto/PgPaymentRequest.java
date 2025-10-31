package com.security.test.model.dto;

import java.math.BigDecimal;

/**
 * PG사 결제 승인 요청 DTO
 * 외부 PG사 API로 전송하는 요청 데이터
 */
public class PgPaymentRequest {

    private String merchantId;        // 가맹점 ID
    private String orderId;           // 주문 ID
    private BigDecimal amount;        // 결제 금액
    private String currency;          // 통화 (KRW, USD 등)
    private String paymentMethod;     // 결제 수단 (card, transfer 등)
    private String customerName;      // 고객명
    private String customerEmail;     // 고객 이메일
    private CardInfo cardInfo;        // 카드 정보

    public PgPaymentRequest() {}

    public PgPaymentRequest(String merchantId, String orderId, BigDecimal amount,
                           String currency, String paymentMethod, String customerName,
                           String customerEmail, CardInfo cardInfo) {
        this.merchantId = merchantId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.cardInfo = cardInfo;
    }

    // Getters and Setters
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public CardInfo getCardInfo() { return cardInfo; }
    public void setCardInfo(CardInfo cardInfo) { this.cardInfo = cardInfo; }

    /**
     * 카드 정보 내부 클래스
     */
    public static class CardInfo {
        private String cardNumber;        // 카드 번호
        private String expiryMonth;       // 유효기간 월
        private String expiryYear;        // 유효기간 년
        private String cvv;               // CVV

        public CardInfo() {}

        public CardInfo(String cardNumber, String expiryMonth, String expiryYear, String cvv) {
            this.cardNumber = cardNumber;
            this.expiryMonth = expiryMonth;
            this.expiryYear = expiryYear;
            this.cvv = cvv;
        }

        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

        public String getExpiryMonth() { return expiryMonth; }
        public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }

        public String getExpiryYear() { return expiryYear; }
        public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }

        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
    }
}
