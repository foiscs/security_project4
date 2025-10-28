#!/bin/bash
# EC2 User Data Script for RDS Environment Variables
snap install amazon-ssm-agent --classic
systemctl enable --now snap.amazon-ssm-agent.amazon-ssm-agent.service

# Create setenv.sh for Tomcat
cat <<'EOF' > /opt/tomcat/bin/setenv.sh
#!/bin/bash
export RDS_ENDPOINT="${rds_endpoint}"
export DB_NAME="${rds_database_name}"
export DB_USERNAME="${rds_username}"
export DB_PASSWORD="${rds_password}"
EOF

# Make it executable
chmod +x /opt/tomcat/bin/setenv.sh

# Restart Tomcat to apply changes
systemctl restart tomcat || service tomcat restart

# Log completion
echo "RDS environment variables configured successfully" >> /var/log/user-data.log



# ================= UTF-8 강제 + web.xml 필터 (추가) =================

# 1) systemd drop-in으로 JVM UTF-8 강제
install -d -m 0755 /etc/systemd/system/tomcat.service.d
tee /etc/systemd/system/tomcat.service.d/utf8.conf >/dev/null <<'EOF'
[Service]
Environment="LANG=C.UTF-8"
Environment="LC_ALL=C.UTF-8"
Environment="JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8"
Environment="CATALINA_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
EOF

# 2) Tomcat 전역 web.xml에 SetCharacterEncodingFilter 삽입(없을 때만)
cp /opt/tomcat/conf/web.xml /opt/tomcat/conf/web.xml.bak.$(date +%F-%H%M%S) 2>/dev/null || true
bash -lc '
f=/opt/tomcat/conf/web.xml
grep -q "SetCharacterEncodingFilter" "$f" && exit 0
tmp=$(mktemp)
awk "
  /<\/web-app>/ && !done {
    print \"  <filter>\"
    print \"    <filter-name>SetCharacterEncodingFilter</filter-name>\"
    print \"    <filter-class>org.apache.catalina.filters.SetCharacterEncodingFilter</filter-class>\"
    print \"    <init-param><param-name>requestEncoding</param-name><param-value>UTF-8</param-value></init-param>\"
    print \"    <init-param><param-name>responseEncoding</param-name><param-value>UTF-8</param-value></init-param>\"
    print \"    <init-param><param-name>forceRequestEncoding</param-name><param-value>true</param-value></init-param>\"
    print \"    <init-param><param-name>forceResponseEncoding</param-name><param-value>true</param-value></init-param>\"
    print \"  </filter>\"
    print \"  <filter-mapping>\"
    print \"    <filter-name>SetCharacterEncodingFilter</filter-name>\"
    print \"    <url-pattern>/*</url-pattern>\"
    print \"  </filter-mapping>\"
    done=1
  }
  { print }
" "$f" > "$tmp" && mv "$tmp" "$f"
'

# 3) 적용
systemctl daemon-reload
systemctl restart tomcat || service tomcat restart
echo "UTF-8 encoding enforcement applied" >> /var/log/user-data.log
# ===================================================================