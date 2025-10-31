package com.security.test.service;

import com.security.test.model.dto.PgPaymentRequest;
import com.security.test.model.dto.PgPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 외부 PG사 API 호출 서비스
 *
 * ⚠️ 주의: 이 코드는 실제로 외부 API를 호출하지 않습니다.
 * 교육/테스트 목적으로 PG API 호출을 시뮬레이션합니다.
 */
@Service
public class PgApiService {

    private static final Logger logger = LoggerFactory.getLogger(PgApiService.class);

    // 가상의 PG사 API 정보
    private static final String PG_API_URL = "https://api.example-pg.com/v1/payments";
    private static final String API_KEY = "test_sk_ABCdefGHIjklMNOpqrSTUvwxYZ123456";  // 임의의 API 키
    private static final String MERCHANT_ID = "MID_hyundai_20251031";                  // 임의의 가맹점 ID

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 결제 승인 요청
     *
     * @param orderId 주문 ID
     * @param amount 결제 금액
     * @param currency 통화
     * @param paymentMethod 결제 수단
     * @param customerName 고객명
     * @param customerEmail 고객 이메일
     * @return PG사 결제 응답
     */
    public PgPaymentResponse approvePayment(String orderId, BigDecimal amount, String currency,
                                           String paymentMethod, String customerName, String customerEmail) {

        logger.info("=== PG API 호출 시작 ===");
        logger.info("PG API URL: {}", PG_API_URL);
        logger.info("주문 ID: {}, 금액: {} {}", orderId, amount, currency);

        try {
            // 1. 요청 데이터 생성
            PgPaymentRequest request = createPaymentRequest(orderId, amount, currency,
                                                            paymentMethod, customerName, customerEmail);

            // 2. HTTP 헤더 설정 (API 키 포함)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("X-Merchant-ID", MERCHANT_ID);

            // 3. HTTP 엔티티 생성
            HttpEntity<PgPaymentRequest> entity = new HttpEntity<>(request, headers);

            logger.info("요청 헤더: Authorization: Bearer {}", maskApiKey(API_KEY));
            logger.info("요청 헤더: X-Merchant-ID: {}", MERCHANT_ID);
            logger.info("요청 본문: {}", request);

            // 4. 실제로는 외부 API 호출 (주석 처리)
            // ResponseEntity<PgPaymentResponse> response = restTemplate.postForEntity(
            //     PG_API_URL, entity, PgPaymentResponse.class
            // );

            // 5. 시뮬레이션: 가상의 성공 응답 생성
            PgPaymentResponse mockResponse = createMockSuccessResponse(orderId, amount, currency, paymentMethod);

            logger.info("=== PG API 응답 수신 (모의) ===");
            logger.info("거래 ID: {}", mockResponse.getTransactionId());
            logger.info("결과 코드: {}", mockResponse.getResultCode());
            logger.info("결과 메시지: {}", mockResponse.getResultMessage());
            logger.info("결제 상태: {}", mockResponse.getStatus());

            return mockResponse;

        } catch (Exception e) {
            logger.error("PG API 호출 실패: {}", e.getMessage(), e);

            // 실패 응답 생성
            PgPaymentResponse errorResponse = new PgPaymentResponse();
            errorResponse.setResultCode("E999");
            errorResponse.setResultMessage("결제 처리 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.setOrderId(orderId);
            errorResponse.setStatus("FAILED");

            return errorResponse;
        }
    }

    /**
     * 결제 취소 요청
     *
     * @param transactionId PG사 거래 ID
     * @param amount 취소 금액
     * @param reason 취소 사유
     * @return PG사 응답
     */
    public PgPaymentResponse cancelPayment(String transactionId, BigDecimal amount, String reason) {

        logger.info("=== PG API 결제 취소 호출 시작 ===");
        logger.info("거래 ID: {}, 취소 금액: {}", transactionId, amount);
        logger.info("취소 사유: {}", reason);

        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("X-Merchant-ID", MERCHANT_ID);

            // 취소 요청 URL
            String cancelUrl = PG_API_URL + "/" + transactionId + "/cancel";

            logger.info("취소 요청 URL: {}", cancelUrl);

            // 실제 API 호출은 주석 처리
            // ResponseEntity<PgPaymentResponse> response = restTemplate.exchange(
            //     cancelUrl, HttpMethod.POST, new HttpEntity<>(headers), PgPaymentResponse.class
            // );

            // 시뮬레이션: 가상의 취소 성공 응답
            PgPaymentResponse mockResponse = new PgPaymentResponse();
            mockResponse.setResultCode("0000");
            mockResponse.setResultMessage("결제 취소 성공");
            mockResponse.setTransactionId(transactionId);
            mockResponse.setAmount(amount);
            mockResponse.setStatus("CANCELLED");
            mockResponse.setApprovedAt(LocalDateTime.now());

            logger.info("결제 취소 완료 (모의): {}", mockResponse.getStatus());

            return mockResponse;

        } catch (Exception e) {
            logger.error("PG API 결제 취소 실패: {}", e.getMessage(), e);

            PgPaymentResponse errorResponse = new PgPaymentResponse();
            errorResponse.setResultCode("E999");
            errorResponse.setResultMessage("결제 취소 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.setTransactionId(transactionId);
            errorResponse.setStatus("CANCEL_FAILED");

            return errorResponse;
        }
    }

    /**
     * PG 결제 요청 객체 생성
     */
    private PgPaymentRequest createPaymentRequest(String orderId, BigDecimal amount, String currency,
                                                  String paymentMethod, String customerName, String customerEmail) {

        // 테스트용 가상의 카드 정보
        PgPaymentRequest.CardInfo cardInfo = new PgPaymentRequest.CardInfo(
            "1234-5678-9012-3456",  // 카드 번호 (테스트)
            "12",                    // 유효기간 월
            "25",                    // 유효기간 년
            "123"                    // CVV
        );

        return new PgPaymentRequest(
            MERCHANT_ID,
            orderId,
            amount,
            currency,
            paymentMethod,
            customerName,
            customerEmail,
            cardInfo
        );
    }

    /**
     * 가상의 성공 응답 생성 (시뮬레이션용)
     */
    private PgPaymentResponse createMockSuccessResponse(String orderId, BigDecimal amount,
                                                        String currency, String paymentMethod) {

        PgPaymentResponse response = new PgPaymentResponse();
        response.setResultCode("0000");  // 성공 코드
        response.setResultMessage("결제 승인 완료");
        response.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 18));
        response.setOrderId(orderId);
        response.setAmount(amount);
        response.setCurrency(currency);
        response.setPaymentMethod(paymentMethod);
        response.setStatus("APPROVED");
        response.setApprovedAt(LocalDateTime.now());
        response.setCardCompany("현대카드");
        response.setCardNumber("1234-56**-****-3456");
        response.setInstallment("00");  // 일시불

        return response;
    }

    /**
     * API 키 마스킹 (로그 출력용)
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "****";
        }
        return apiKey.substring(0, 10) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
