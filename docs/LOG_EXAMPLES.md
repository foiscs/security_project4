# Spring 애플리케이션 로그 예시

## 목차
1. [기본 Spring MVC 로그](#1-기본-spring-mvc-로그)
2. [RestTemplate 외부 API 호출 로그](#2-resttemplate-외부-api-호출-로그)
3. [Hibernate SQL 로그](#3-hibernate-sql-로그)
4. [PG API 서비스 로그](#4-pg-api-서비스-로그)
5. [전체 결제 흐름 로그 예시](#5-전체-결제-흐름-로그-예시)

---

## 1. 기본 Spring MVC 로그

### 설정
```properties
logging.level.org.springframework.web=DEBUG
```

### 로그 출력 예시

```log
2025-10-31 10:15:32.123 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : POST "/api/payments", parameters={}
2025-10-31 10:15:32.125 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.security.test.controller.PaymentController#createPayment(CreatePaymentRequest)
2025-10-31 10:15:32.127 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestResponseBodyMethodProcessor : Read "application/json" to [CreatePaymentRequest@7f31245a]
2025-10-31 10:15:32.890 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestResponseBodyMethodProcessor : Using 'application/json', given [*/*] and supported [application/json, application/*+json]
2025-10-31 10:15:32.892 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestResponseBodyMethodProcessor : Writing [{success=true, message=결제가 성공적으로 처리되었습니다., payment=...}]
2025-10-31 10:15:32.895 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : Completed 201 CREATED
```

**포함되는 정보:**
- ✅ 요청 메서드 및 경로 (`POST /api/payments`)
- ✅ 매핑된 컨트롤러 메서드
- ✅ HTTP 상태 코드 (`201 CREATED`)
- ❌ 요청 본문 (Request Body)
- ❌ 응답 본문 (Response Body)

---

## 2. RestTemplate 외부 API 호출 로그

### 설정
```properties
logging.level.com.security.test.config.RestTemplateLoggingInterceptor=INFO
```

### 인터셉터 적용 후 로그 출력

```log
2025-10-31 10:15:32.500 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : === RestTemplate 요청 ===
2025-10-31 10:15:32.501 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : URI: POST https://api.example-pg.com/v1/payments
2025-10-31 10:15:32.502 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Headers: [Content-Type:"application/json", Authorization:"Bearer ****", X-Merchant-ID:"MID_hyundai_20251031"]
2025-10-31 10:15:32.503 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Request Body: {"merchantId":"MID_hyundai_20251031","orderId":"ORDER_rental-123_1730344532500","amount":50000,"currency":"KRW","paymentMethod":"card","customerName":"홍길동","customerEmail":"hong@example.com","cardInfo":{"cardNumber":"****","expiryMonth":"12","expiryYear":"25","cvv":"***"}}

2025-10-31 10:15:32.650 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : === RestTemplate 응답 ===
2025-10-31 10:15:32.651 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Status Code: 200 OK
2025-10-31 10:15:32.652 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Status Text: OK
2025-10-31 10:15:32.653 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Headers: [Content-Type:"application/json;charset=UTF-8", Date:"Thu, 31 Oct 2025 01:15:32 GMT"]
2025-10-31 10:15:32.654 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Response Body: {"resultCode":"0000","resultMessage":"결제 승인 완료","transactionId":"TXN_a1b2c3d4e5f6g7h8","orderId":"ORDER_rental-123_1730344532500","amount":50000,"currency":"KRW","paymentMethod":"card","status":"APPROVED","approvedAt":"2025-10-31T10:15:32.645","cardCompany":"현대카드","cardNumber":"1234-56**-****-3456","installment":"00"}
```

**포함되는 정보:**
- ✅ 외부 API 엔드포인트 URL
- ✅ HTTP 메서드 (POST)
- ✅ 요청 헤더 (Authorization 마스킹됨)
- ✅ 요청 본문 (카드번호, CVV 마스킹됨)
- ✅ 응답 상태 코드
- ✅ 응답 본문 (PG사 응답)

---

## 3. Hibernate SQL 로그

### 설정
```properties
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 로그 출력 예시

```log
2025-10-31 10:15:32.700 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL :
    select
        rental0_.rental_id as rental_i1_5_0_,
        rental0_.start_actual as start_ac2_5_0_,
        rental0_.end_actual as end_actu3_5_0_
    from
        rentals rental0_
    where
        rental0_.rental_id=?

2025-10-31 10:15:32.701 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [1] as [VARCHAR] - [rental-123]

2025-10-31 10:15:32.750 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL :
    insert
    into
        payments
        (amount, currency, method, pg_provider, pg_tx_id, status, rental_id, user_id, payment_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?)

2025-10-31 10:15:32.751 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [1] as [NUMERIC] - [50000]
2025-10-31 10:15:32.752 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [2] as [VARCHAR] - [KRW]
2025-10-31 10:15:32.753 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [3] as [VARCHAR] - [card]
2025-10-31 10:15:32.754 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [4] as [VARCHAR] - [EXAMPLE_PG]
2025-10-31 10:15:32.755 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [5] as [VARCHAR] - [TXN_a1b2c3d4e5f6g7h8]
2025-10-31 10:15:32.756 TRACE [http-nio-8080-ex ec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [6] as [VARCHAR] - [CAPTURED]
2025-10-31 10:15:32.757 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [7] as [VARCHAR] - [rental-123]
2025-10-31 10:15:32.758 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [8] as [VARCHAR] - [user-456]
2025-10-31 10:15:32.759 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [9] as [VARCHAR] - [payment-789]
```

**포함되는 정보:**
- ✅ 실행된 SQL 쿼리
- ✅ 바인딩된 파라미터 값
- ✅ 데이터베이스 작업 타이밍

---

## 4. PG API 서비스 로그

### 설정
```properties
logging.level.com.security.test.service.PgApiService=INFO
logging.level.com.security.test.service.PaymentService=INFO
logging.level.com.security.test.controller.PaymentController=INFO
```

### 로그 출력 예시

```log
2025-10-31 10:15:32.123 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : === 결제 생성 API 호출 ===
2025-10-31 10:15:32.124 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 대여 ID: rental-123, 금액: 50000 KRW
2025-10-31 10:15:32.125 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 대여 정보 조회 성공: 사용자 ID user-456

2025-10-31 10:15:32.150 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : === 대여 결제 생성 시작 ===
2025-10-31 10:15:32.151 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 대여 ID: rental-123, 사용자 ID: user-456
2025-10-31 10:15:32.200 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 계산된 결제 금액: 50000 원 (기본: 50000, 연체료: 0)

2025-10-31 10:15:32.250 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : === PG API 호출 시작 ===
2025-10-31 10:15:32.251 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : PG API URL: https://api.example-pg.com/v1/payments
2025-10-31 10:15:32.252 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 주문 ID: ORDER_rental-123_1730344532250, 금액: 50000 KRW
2025-10-31 10:15:32.300 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 요청 헤더: Authorization: Bearer test_sk_AB****3456
2025-10-31 10:15:32.301 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 요청 헤더: X-Merchant-ID: MID_hyundai_20251031
2025-10-31 10:15:32.302 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 요청 본문: PgPaymentRequest@1a2b3c4d

2025-10-31 10:15:32.650 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : === PG API 응답 수신 (모의) ===
2025-10-31 10:15:32.651 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 거래 ID: TXN_a1b2c3d4e5f6g7h8
2025-10-31 10:15:32.652 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 결과 코드: 0000
2025-10-31 10:15:32.653 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 결과 메시지: 결제 승인 완료
2025-10-31 10:15:32.654 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 결제 상태: APPROVED

2025-10-31 10:15:32.800 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : PG 결제 승인 성공: 거래 ID TXN_a1b2c3d4e5f6g7h8
2025-10-31 10:15:32.850 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 결제 정보 저장 완료: Payment ID payment-789

2025-10-31 10:15:32.900 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 결제 생성 성공: Payment ID payment-789
```

---

## 5. 전체 결제 흐름 로그 예시

### 클라이언트 요청
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "rentalId": "rental-123",
    "amount": 50000,
    "currency": "KRW",
    "paymentMethod": "card",
    "customerName": "홍길동",
    "customerEmail": "hong@example.com"
  }'
```

### 완전한 로그 흐름

```log
# ===== 1. Spring MVC 요청 수신 =====
2025-10-31 10:15:32.123 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : POST "/api/payments", parameters={}
2025-10-31 10:15:32.125 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.security.test.controller.PaymentController#createPayment(CreatePaymentRequest)

# ===== 2. PaymentController 처리 시작 =====
2025-10-31 10:15:32.127 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : === 결제 생성 API 호출 ===
2025-10-31 10:15:32.128 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 대여 ID: rental-123, 금액: 50000 KRW

# ===== 3. 대여 정보 조회 (Hibernate) =====
2025-10-31 10:15:32.130 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL :
    select rental0_.rental_id as rental_i1_5_0_ from rentals rental0_ where rental0_.rental_id=?
2025-10-31 10:15:32.131 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [1] as [VARCHAR] - [rental-123]
2025-10-31 10:15:32.140 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 대여 정보 조회 성공: 사용자 ID user-456

# ===== 4. PaymentService 처리 =====
2025-10-31 10:15:32.150 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : === 대여 결제 생성 시작 ===
2025-10-31 10:15:32.151 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 대여 ID: rental-123, 사용자 ID: user-456
2025-10-31 10:15:32.200 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 계산된 결제 금액: 50000 원 (기본: 50000, 연체료: 0)

# ===== 5. PG API 호출 시작 =====
2025-10-31 10:15:32.250 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : === PG API 호출 시작 ===
2025-10-31 10:15:32.251 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : PG API URL: https://api.example-pg.com/v1/payments
2025-10-31 10:15:32.252 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 주문 ID: ORDER_rental-123_1730344532250, 금액: 50000 KRW
2025-10-31 10:15:32.300 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 요청 헤더: Authorization: Bearer test_sk_AB****3456
2025-10-31 10:15:32.301 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 요청 헤더: X-Merchant-ID: MID_hyundai_20251031

# ===== 6. RestTemplate 요청 로그 (인터셉터) =====
2025-10-31 10:15:32.500 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : === RestTemplate 요청 ===
2025-10-31 10:15:32.501 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : URI: POST https://api.example-pg.com/v1/payments
2025-10-31 10:15:32.502 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Headers: [Content-Type:"application/json", Authorization:"Bearer ****", X-Merchant-ID:"MID_hyundai_20251031"]
2025-10-31 10:15:32.503 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Request Body: {"merchantId":"MID_hyundai_20251031","orderId":"ORDER_rental-123_1730344532500","amount":50000,"currency":"KRW","paymentMethod":"card","customerName":"홍길동","customerEmail":"hong@example.com","cardInfo":{"cardNumber":"****","expiryMonth":"12","expiryYear":"25","cvv":"***"}}

# ===== 7. RestTemplate 응답 로그 (인터셉터) =====
2025-10-31 10:15:32.650 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : === RestTemplate 응답 ===
2025-10-31 10:15:32.651 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Status Code: 200 OK
2025-10-31 10:15:32.654 INFO [http-nio-8080-exec-1] c.s.t.c.RestTemplateLoggingInterceptor : Response Body: {"resultCode":"0000","resultMessage":"결제 승인 완료","transactionId":"TXN_a1b2c3d4e5f6g7h8","orderId":"ORDER_rental-123_1730344532500","amount":50000,"currency":"KRW","paymentMethod":"card","status":"APPROVED","approvedAt":"2025-10-31T10:15:32.645","cardCompany":"현대카드","cardNumber":"1234-56**-****-3456","installment":"00"}

# ===== 8. PG API 응답 처리 =====
2025-10-31 10:15:32.655 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : === PG API 응답 수신 (모의) ===
2025-10-31 10:15:32.656 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 거래 ID: TXN_a1b2c3d4e5f6g7h8
2025-10-31 10:15:32.657 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 결과 코드: 0000
2025-10-31 10:15:32.658 INFO [http-nio-8080-exec-1] c.s.t.service.PgApiService : 결과 메시지: 결제 승인 완료

# ===== 9. Payment 엔티티 저장 (Hibernate) =====
2025-10-31 10:15:32.700 DEBUG [http-nio-8080-exec-1] org.hibernate.SQL :
    insert into payments (amount, currency, method, pg_provider, pg_tx_id, status, rental_id, user_id, payment_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
2025-10-31 10:15:32.701 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [1] as [NUMERIC] - [50000]
2025-10-31 10:15:32.702 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [2] as [VARCHAR] - [KRW]
2025-10-31 10:15:32.703 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [5] as [VARCHAR] - [TXN_a1b2c3d4e5f6g7h8]
2025-10-31 10:15:32.704 TRACE [http-nio-8080-exec-1] o.h.type.descriptor.sql.BasicBinder : binding parameter [6] as [VARCHAR] - [CAPTURED]

# ===== 10. PaymentService 완료 =====
2025-10-31 10:15:32.800 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : PG 결제 승인 성공: 거래 ID TXN_a1b2c3d4e5f6g7h8
2025-10-31 10:15:32.850 INFO [http-nio-8080-exec-1] c.s.t.service.PaymentService : 결제 정보 저장 완료: Payment ID payment-789

# ===== 11. PaymentController 응답 =====
2025-10-31 10:15:32.900 INFO [http-nio-8080-exec-1] c.s.t.controller.PaymentController : 결제 생성 성공: Payment ID payment-789

# ===== 12. Spring MVC 응답 =====
2025-10-31 10:15:32.950 DEBUG [http-nio-8080-exec-1] o.s.w.s.m.m.a.RequestResponseBodyMethodProcessor : Writing [{success=true, message=결제가 성공적으로 처리되었습니다., payment=...}]
2025-10-31 10:15:32.990 DEBUG [http-nio-8080-exec-1] o.s.web.servlet.DispatcherServlet : Completed 201 CREATED
```

---

## 6. 공격자가 로그에서 확인할 수 있는 정보

### 6.1 API 키 및 인증 정보

```bash
# 로그 파일에서 API 키 검색
grep -r "test_sk_\|MID_\|Bearer" /var/log/spring-app/

# 출력:
# 요청 헤더: Authorization: Bearer test_sk_AB****3456
# 요청 헤더: X-Merchant-ID: MID_hyundai_20251031
```

**문제점**: API 키가 마스킹되어 있지만 **원본 PgApiService 로그에는 마스킹되지 않은 키가 있을 수 있음**

### 6.2 RestTemplate 인터셉터 로그에서 완전한 요청/응답

```bash
# RestTemplate 로그에서 전체 요청 본문 확인
grep -A 10 "RestTemplate 요청" /var/log/spring-app/application.log

# PG API 엔드포인트
# URI: POST https://api.example-pg.com/v1/payments

# Merchant ID
# X-Merchant-ID: MID_hyundai_20251031

# 거래 ID
# transactionId: TXN_a1b2c3d4e5f6g7h8
```

### 6.3 데이터베이스 쿼리 및 값

```bash
# Hibernate 로그에서 실제 데이터 확인
grep -A 20 "insert into payments" /var/log/spring-app/application.log

# binding parameter [5] as [VARCHAR] - [TXN_a1b2c3d4e5f6g7h8]  <- PG 거래 ID
# binding parameter [7] as [VARCHAR] - [rental-123]           <- 대여 ID
# binding parameter [8] as [VARCHAR] - [user-456]             <- 사용자 ID
```

---

## 7. 로그 보안 강화 권장 사항

### 7.1 민감 정보 마스킹

- ✅ API 키: `test_sk_****`
- ✅ 카드 번호: `****-****-****-1234`
- ✅ CVV: `***`
- ❌ 거래 ID: 그대로 노출 (추적 필요)
- ❌ Merchant ID: 그대로 노출 (공개 정보)

### 7.2 운영 환경 로그 레벨 조정

```properties
# 운영 환경 (application-prod.properties)
logging.level.root=WARN
logging.level.com.security.test=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN

# RestTemplate 로깅도 최소화
logging.level.com.security.test.config.RestTemplateLoggingInterceptor=WARN
```

### 7.3 CloudWatch Logs 필터링

민감 정보를 제외하고 CloudWatch로 전송:
```json
{
  "filter_pattern": "[time, request_id, level != DEBUG, ...]"
}
```

---

**완료!** 이제 Spring 애플리케이션에서 생성되는 모든 로그의 종류와 내용을 확인할 수 있습니다.
