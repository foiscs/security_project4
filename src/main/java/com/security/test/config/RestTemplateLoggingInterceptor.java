package com.security.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * RestTemplate 요청/응답 로깅 인터셉터
 *
 * 모든 외부 API 호출(PG API 등)의 요청/응답을 자동으로 기록합니다.
 * ⚠️ 운영 환경에서는 민감 정보(API 키, 카드 번호 등)를 마스킹해야 합니다.
 */
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        // 요청 로깅
        logRequest(request, body);

        // 실제 요청 실행
        ClientHttpResponse response = execution.execute(request, body);

        // 응답 로깅
        logResponse(response);

        return response;
    }

    /**
     * HTTP 요청 로깅
     */
    private void logRequest(HttpRequest request, byte[] body) {
        logger.info("=== RestTemplate 요청 ===");
        logger.info("URI: {} {}", request.getMethod(), request.getURI());
        logger.info("Headers: {}", maskSensitiveHeaders(request.getHeaders().toString()));

        if (body.length > 0) {
            String requestBody = new String(body, StandardCharsets.UTF_8);
            logger.info("Request Body: {}", maskSensitiveData(requestBody));
        }
    }

    /**
     * HTTP 응답 로깅
     */
    private void logResponse(ClientHttpResponse response) throws IOException {
        logger.info("RestTemplate 응답");
        logger.info("Status Code: {}", response.getStatusCode());
        logger.info("Status Text: {}", response.getStatusText());
        logger.info("Headers: {}", response.getHeaders());

        // 응답 본문 읽기 (주의: 한 번만 읽을 수 있음)
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            String responseBody = reader.lines().collect(Collectors.joining("\n"));
            if (!responseBody.isEmpty()) {
                logger.info("Response Body: {}", responseBody);
            }
        }
    }

    /**
     * 민감한 헤더 정보 마스킹 (Authorization, API Key 등)
     */
    private String maskSensitiveHeaders(String headers) {
        return headers
                .replaceAll("(Bearer\\s+)[A-Za-z0-9_-]+", "$1****")
                .replaceAll("(api[_-]?key[\"'\\s:=]+)[A-Za-z0-9_-]+", "$1****");
    }

    /**
     * 민감한 데이터 마스킹 (카드 번호, CVV 등)
     */
    private String maskSensitiveData(String data) {
        return data
                .replaceAll("(\"cardNumber\"\\s*:\\s*\")[0-9-]+", "$1****")
                .replaceAll("(\"cvv\"\\s*:\\s*\")[0-9]+", "$1***")
                .replaceAll("(test_sk_)[A-Za-z0-9]+", "$1****");
    }
}
