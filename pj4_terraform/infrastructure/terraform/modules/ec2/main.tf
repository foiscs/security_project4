# infrastructure/terraform/modules/ec2/main.tf


# Launch Template for web ASG
resource "aws_launch_template" "web" {
  name_prefix   = "${var.project_name}-web-"
  image_id      = var.web_ami_id
  instance_type = "t3.micro"
  update_default_version = true 
  vpc_security_group_ids = [aws_security_group.web.id]

  # 인스턴스 프로파일 추가
  iam_instance_profile { name = aws_iam_instance_profile.web.name }

  # User data(base64 인코딩 필수)
  user_data = base64encode(templatefile("${path.module}/user_data_web.sh", {
    app       = var.project_name
    port      = var.application_port
    region    = var.aws_region
    gh_org    = var.gh_org
    gh_repo   = var.gh_repo
    gh_tag    = var.gh_tag
    gh_asset  = var.gh_asset
    ssm_param = var.ssm_github_token_param
  }))


  user_data = base64encode(templatefile("${path.module}/user_data.sh", {
    rds_endpoint      = var.rds_endpoint
    rds_database_name = var.rds_database_name
    rds_username      = var.rds_username
    rds_password      = var.rds_password
  }))

  tag_specifications {
    resource_type = "instance"
    tags = merge(var.common_tags, {
      Name = "${var.project_name}-web"
      Role = "web"
    })
  }
}

# Auto Scaling Group for web (ALB에 연결)
resource "aws_autoscaling_group" "web" {        
  name                      = "${var.project_name}-web-asg"
  max_size                  = 3
  min_size                  = 1
  desired_capacity          = 2
  vpc_zone_identifier       = var.private_subnet_ids
  launch_template {
    id      = aws_launch_template.web.id
    version = aws_launch_template.web.latest_version
  }
  instance_refresh {
    strategy  = "Rolling"
    triggers  = ["launch_template"]
  }
  target_group_arns = [aws_lb_target_group.web.arn] 
  tag {
    key                 = "launch_version"
    value               = "aws_launch_template.web.latest_version"
    propagate_at_launch = true
  }
  dynamic "tag" {
    for_each = [for k, v in var.common_tags : { key = k, value = v }]
    content {
      key                 = tag.value.key
      value               = tag.value.value
      propagate_at_launch = true
    }
  }
}


# 내부용 보안그룹 (관리자/IoT)
# resource "aws_security_group" "internal" {
#  name_prefix = "${var.project_name}-internal-sg"
# description = "Internal servers (admin, iot)"
#  vpc_id      = var.vpc_id
#  ingress {
#    from_port   = 22
#    to_port     = 22
#    protocol    = "tcp"
#    cidr_blocks = var.allowed_ssh_cidrs
#    description = "SSH from allowed CIDRs"
#  }

#  egress {
#    from_port   = 0
#    to_port     = 0
#    protocol    = "-1"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#  tags = var.common_tags
#}

# 관리자와 IoT용 개별 EC2 인스턴스 (각각 private subnet에 생성)
# resource "aws_instance" "admin" {
#   ami                    = var.ami_id
#   instance_type          = "t3.micro"
#   subnet_id              = element(var.private_subnet_ids, 0)
#   vpc_security_group_ids = [aws_security_group.internal.id]
#   tags = merge(var.common_tags, { Name = "${var.project_name}-admin" })
# }





#resource "aws_instance" "iot" {
#  ami                    = var.ami_id
#  instance_type          = "t2.micro"
#  subnet_id              = element(var.private_subnet_ids, 0)
#  vpc_security_group_ids = [aws_security_group.internal.id]
#  tags = merge(var.common_tags, { Name = "${var.project_name}-iot" })
#}

# =========================================
# Bastion Host Security Group (RDS 접근용)
# =========================================

resource "aws_security_group" "bastion" {
  name_prefix = "${var.project_name}-bastion"
  vpc_id      = var.vpc_id
  description = "Security group for bastion host access"
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.allowed_ssh_cidrs
  }
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-bastion-sg"
    Component = "Security"
  })
}

# RDS 연결용 bastion host
resource "aws_instance" "bastion" {
  ami                    = coalesce(var.bastion_ami_id, data.aws_ssm_parameter.al2023.value)
  instance_type          = "t3.micro"
  subnet_id              = element(var.public_subnet_ids, 0)
  vpc_security_group_ids = [aws_security_group.bastion.id]
  tags = merge(var.common_tags, { Name = "${var.project_name}-bastion" })
}

data "aws_ssm_parameter" "al2023" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-6.1-x86_64"
}


resource "aws_security_group" "web" {
  name_prefix = "${var.project_name}-web"
  vpc_id      = var.vpc_id
  description = "Security group for web host access"
  # ALB SG를 '소스'로 해서 앱 포트만 허용
  ingress {
    description = "App traffic from ALB"
    from_port   = var.application_port
    to_port     = var.application_port
    protocol    = "tcp"
    security_groups = [aws_security_group.alb[0].id]
  }
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-web-sg"
    Component = "Security"
  })
}

# =========================================
# Application Load Balancer
# =========================================


resource "aws_lb" "main" {
  count              = var.enable_load_balancer ? 1 : 0
  name               = "${var.project_name}-alb"
  internal           = false
  load_balancer_type = var.lb_type
  security_groups    = [aws_security_group.alb[0].id]
  subnets            = var.public_subnet_ids
  enable_deletion_protection = false
  tags = merge(var.common_tags, {
    Name        = "${var.project_name}-alb"
    Component   = "LoadBalancer"
  })
}


resource "aws_security_group" "alb" {
  count       = var.enable_load_balancer ? 1 : 0
  name_prefix = "${var.project_name}-alb"
  vpc_id      = var.vpc_id
  description = "Security group for Application Load Balancer"
  # HTTP 접근 허용
  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # HTTPS 접근 허용
  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # 모든 outbound 트래픽 허용
  egress {
    description = "All outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-alb-sg"
    Component = "LoadBalancer"
  })
}

# ALB Target Group
resource "aws_lb_target_group" "web" {
  name        = "${var.project_name}-web-tg"
  port        = var.application_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "instance"                  

  health_check {
    enabled             = true
    path                = var.health_check_path
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
    matcher             = "200"
  }
  tags = var.common_tags
}


# ALB Listener
resource "aws_lb_listener" "web" {             
  count             = var.enable_load_balancer ? 1 : 0
  load_balancer_arn = aws_lb.main[0].arn
  port              = "80"
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.web.arn
  }
  tags = merge(var.common_tags, {
    Name      = "${var.project_name}-alb-listener"
    Component = "LoadBalancer"
  })
}


# === EC2가 SSM SecureString을 읽기 위한 최소 IAM 구성 ===
data "aws_caller_identity" "cur" {}

# instance_profile_name 없으면 생성, 있으면 재사용
locals {
  create_ip = var.instance_profile_name == null
}

data "aws_iam_policy_document" "ec2_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals { 
      type = "Service"
      identifiers = ["ec2.amazonaws.com"] 
    }
  }
}

resource "aws_iam_role" "web" {
  name               = "${var.project_name}-web-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_trust.json
  tags               = var.common_tags
}

# /github/token 읽기 권한
# locals {
#   ssm_param_arn = "arn:aws:ssm:${var.aws_region}:${data.aws_caller_identity.cur.account_id}:parameter${var.ssm_github_token_param}"
# }



# SSM SecureString(/github/token 등) 읽기 (필요하면 남기고, 안쓰면 제거)
locals {
  ssm_param_arn = "arn:aws:ssm:${var.aws_region}:${data.aws_caller_identity.cur.account_id}:parameter${var.ssm_github_token_param}"
}

data "aws_iam_policy_document" "ssm_read" {
  statement {
    effect    = "Allow"
    actions   = ["ssm:GetParameter"]
    resources = [local.ssm_param_arn]
  }
}

resource "aws_iam_policy" "ssm_read" {
  name   = "${var.project_name}-web-ssm-read"
  policy = data.aws_iam_policy_document.ssm_read.json
}

# SSM 세션/RunCommand 표준 권한
resource "aws_iam_role_policy_attachment" "ssm_read_attach" {
  role       = aws_iam_role.web.name
  policy_arn = aws_iam_policy.ssm_read.arn
}

# 세션 매니저/RunCommand용
resource "aws_iam_role_policy_attachment" "attach_ssm_core" {
  role       = aws_iam_role.web.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}


# 최종 LT에 연결할 인스턴스 프로파일 이름
locals {
  resolved_instance_profile = local.create_ip ? aws_iam_instance_profile.web.name : var.instance_profile_name
}


# =========================================
# EC2가 사용할 역할 (s3)
# =========================================

# S3 특정 버킷/프리픽스 읽기 전용
data "aws_iam_policy_document" "web_s3_read" {
  statement {
    actions   = ["s3:GetObject","s3:ListBucket"]
    resources = [
      "${var.service_bucket_arn}",
      "${var.service_bucket_arn}/*",
    ]
  }
}
resource "aws_iam_policy" "web_s3_read" {
  name   = "${var.project_name}-web-s3-read"
  policy = data.aws_iam_policy_document.web_s3_read.json
}
resource "aws_iam_role_policy_attachment" "web_s3_read_attach" {
  role       = aws_iam_role.web.name
  policy_arn = aws_iam_policy.web_s3_read.arn
}



data "aws_iam_policy_document" "web_kms_use" {
  statement {
    actions   = ["kms:Decrypt", "kms:DescribeKey", "kms:GenerateDataKey*"]
    resources = [
      "${var.service_bucket_kms_arn}",
      "${var.service_bucket_kms_arn}/*",
    ]
  }
}
resource "aws_iam_policy" "web_kms_use" {
  name   = "${var.project_name}-web-kms-use"
  policy = data.aws_iam_policy_document.web_kms_use.json
}
resource "aws_iam_role_policy_attachment" "web_kms_use_attach" {
  role       = aws_iam_role.web.name
  policy_arn = aws_iam_policy.web_kms_use.arn
}


# Instance Profile (LT에 붙일 것)
resource "aws_iam_instance_profile" "web" {
  name = "${var.project_name}-web-profile"
  role = aws_iam_role.web.name
}