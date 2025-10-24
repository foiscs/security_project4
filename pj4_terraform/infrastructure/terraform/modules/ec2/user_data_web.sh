#!/bin/bash
set -Eeuo pipefail

# === 변수 ===
APP="${app}"          # var.project_name
PORT="${port}"        # var.application_port
REGION="${region}"    # var.aws_region
ORG="${gh_org}"
REPO="${gh_repo}"
TAG="v1.0.0"
ASSET="${gh_asset}"
SSM_PARAM="${ssm_param}"

# (로그를 cloud-init-output.log에 더 친절히 남기고 싶다면 주석 해제)
# exec > >(tee -a /var/log/user-data.log) 2>&1
# set -x

# === 패키지 설치 (awscli, curl, java) ===
if command -v yum >/dev/null 2>&1; then
  yum install -y curl awscli java-17-openjdk
elif command -v apt-get >/dev/null 2>&1; then
  export DEBIAN_FRONTEND=noninteractive
  apt-get update -y
  apt-get install -y curl awscli openjdk-17-jre-headless
else
  echo "No known package manager (yum/apt)."; exit 1
fi

# === (옵션) SSM 에이전트 기동 보장 ===
if command -v systemctl >/dev/null 2>&1; then
  systemctl enable --now amazon-ssm-agent || true
fi

# === GitHub PAT 가져오기 (인스턴스 프로파일에 ssm:GetParameter 권한 필수) ===
TOKEN="$(aws ssm get-parameter \
  --name "$${SSM_PARAM}" \
  --with-decryption \
  --region "$${REGION}" \
  --query 'Parameter.Value' --output text || true)"

if [ -z "$${TOKEN}" ] || [ "$${TOKEN}" = "None" ]; then
  echo "Failed to read SSM token '$${SSM_PARAM}'"; exit 1
fi

# === JAR 다운로드 (실패 시 즉시 중단) ===
mkdir -p "/opt/$${APP}"
curl -sSLf -H "Authorization: Bearer $${TOKEN}" -H "Accept: application/octet-stream" \
  -o "/opt/$${APP}/$${APP}.jar" \ 
  "https://github.com/$${ORG}/$${REPO}/releases/download/$${TAG}/$${ASSET}"
test -s "/opt/$${APP}/$${APP}.jar" || { echo "JAR missing"; exit 1; }
unset TOKEN

# === systemd 서비스 생성 ===
cat >/etc/systemd/system/$${APP}.service <<EOF
[Unit]
Description=$${APP}
After=network.target
[Service]
WorkingDirectory=/opt/$${APP}
ExecStart=/usr/bin/java -jar /opt/$${APP}/$${APP}.jar --server.port=$${PORT}
Restart=always
RestartSec=5
[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable --now "$${APP}.service"
