# infrastructure/terraform/modules/ec2/main.tf

# Launch Template for web ASG
resource "aws_launch_template" "web" {
  name_prefix   = "${var.project_name}-web-"
  image_id      = var.ami_id
  instance_type = "t3.micro"
  vpc_security_group_ids = [aws_security_group.web.id]
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
    version = "$Latest"
  }
  instance_refresh {
    strategy  = "Rolling"
    triggers  = ["launch_template"]   # LT 변경 시 자동 롤링
    preferences {
      min_healthy_percentage = 90
      instance_warmup        = 60
    }
  }
  target_group_arns = [aws_lb_target_group.web.arn] 
  tag {
    key                 = "Name"
    value               = "${var.project_name}-web"
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
  ami                    = var.ami_id
  instance_type          = "t3.micro"
  subnet_id              = element(var.public_subnet_ids, 0)
  vpc_security_group_ids = [aws_security_group.bastion.id]
  tags = merge(var.common_tags, { Name = "${var.project_name}-bastion" })
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