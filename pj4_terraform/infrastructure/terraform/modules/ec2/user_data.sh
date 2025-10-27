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
