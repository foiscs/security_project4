
# =========================================
# EC2 Module - ALB, ASG, TG, EC2
# =========================================

module "ec2"{
  source = "./modules/ec2"
  project_name        = var.project_name
  application_port    = var.application_port
  health_check_path   = var.health_check_path
  allowed_ssh_cidrs   = var.allowed_ssh_cidrs
  enable_load_balancer= true
  lb_type             = var.lb_type
  vpc_id              = module.vpc.vpc_id
  public_subnet_ids   = module.vpc.public_subnet_ids
  private_subnet_ids  = module.vpc.private_subnet_ids
  # ami_id            = data.aws_ami.web.id
  web_ami_id          = var.web_ami_id
  common_tags = merge(var.common_tags, {
    Component = "Networking"
  })
}


# =========================================
# Complete AWS Infrastructure Configuration
# =========================================



# Data sources
data "aws_caller_identity" "current" {}
data "aws_region" "current" {}
data "aws_availability_zones" "available" {
  state = "available"
}

# =========================================
# VPC Module - 네트워크 기반 구성
# =========================================

module "vpc" {
  source = "./modules/vpc"
  project_name        = var.project_name
  vpc_cidr            = var.vpc_cidr
  public_subnet_cidrs = var.public_subnets
  private_subnet_cidrs = var.private_subnets
  database_subnet_cidrs = var.database_subnets
  enable_nat_gateway     = true
  enable_dns_hostnames   = true
  enable_dns_support     = true
  enable_vpc_flow_logs   = var.enable_vpc_flow_logs

  common_tags = merge(var.common_tags, {
    Component = "Networking"
  })
}


# =========================================
# KMS Key for Encryption (중앙 집중식)
# =========================================

resource "aws_kms_key" "main" {
  description             = "KMS key for ${var.project_name}"
  deletion_window_in_days = 7
  enable_key_rotation     = true

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "Enable IAM User Permissions"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
        }
        Action   = "kms:*"
        Resource = "*"
      },
      {
        Sid    = "Allow EKS Service"
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
        Action = [
          "kms:Decrypt",
          "kms:GenerateDataKey"
        ]
        Resource = "*"
      }
    ]
  })

  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-kms"
    Component = "Security"
  })
}

resource "aws_kms_alias" "main" {
  name          = "alias/${var.project_name}-test"
  target_key_id = aws_kms_key.main.key_id
}



# =========================================
# RDS Module - 관계형 데이터베이스
# =========================================

module "rds" {
  source = "./modules/rds"
  project_name = var.project_name

  # 네트워크 설정
  vpc_id                = module.vpc.vpc_id
  database_subnet_ids   = module.vpc.database_subnet_ids
  allowed_security_groups = compact([
    module.ec2.web_security_group_id,
    module.ec2.bastion_security_group_id,
  ])
  # 데이터베이스 설정
  engine                 = "mysql"
  engine_version         = "8.0.42"
  instance_class         = var.rds_instance_class
  allocated_storage      = 20
  max_allocated_storage  = 100

  # 인증 설정
  database_name     = var.db_name
  master_username   = var.db_username
  master_password   = var.db_password

  # 백업 설정
  backup_retention_period = var.backup_retention_days
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"

  # 모니터링 설정
  monitoring_interval = 60
  enabled_cloudwatch_logs_exports = ["error", "general", "slowquery"]
  log_retention_days = var.security_log_retention_days


  # Multi-AZ 설정
  multi_az = var.enable_multi_az


  # 보안 설정
  deletion_protection = false


  common_tags = merge(var.common_tags, {
    Component = "Database"
  })
  depends_on = [module.vpc]


}




# =========================================
# SNS Topic for Security Alerts
# =========================================

resource "aws_sns_topic" "security_alerts" {
  name = "${var.project_name}-security-alerts"
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-security-alerts"
    Component = "Notifications"
  })
}


resource "aws_sns_topic_subscription" "email_alerts" {
  count     = length(var.alert_email_addresses)
  topic_arn = aws_sns_topic.security_alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email_addresses[count.index]
}



# =========================================
# CloudWatch Log Groups (중앙 집중식)
# =========================================


resource "aws_cloudwatch_log_group" "application_logs" {
  name              = "/aws/application/${var.project_name}"
  retention_in_days = var.log_retention_days
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-app-logs"
    Component = "Monitoring"
  })
}


resource "aws_cloudwatch_log_group" "security_logs" {
  name              = "/aws/security/${var.project_name}"
  retention_in_days = var.security_log_retention_days
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-security-logs"
    Component = "Monitoring"
  })
}