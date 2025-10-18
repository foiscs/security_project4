# infrastructure/terraform/modules/ec2/variables.tf

variable "project_name" {
  description = "프로젝트 이름 (리소스 명명에 사용)"
  type        = string
  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.project_name))
    error_message = "프로젝트 이름은 소문자, 숫자, 하이픈만 포함할 수 있습니다."
  }
}

variable "environment" {
  description = "환경 구분 (dev, staging, prod)"
  type        = string
  default     = "dev"
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "환경은 dev, staging, prod 중 하나여야 합니다."
  }
}

variable "application_port" {
  description = "애플리케이션 포트"
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "헬스 체크 경로"
  type        = string
  default     = "/"
}

variable "allowed_ssh_cidrs" {
  description = "SSH 접근을 허용할 CIDR 블록들"
  type        = list(string)
  default     = ["10.0.0.0/16"]  # VPC 내부만 허용
}


variable "enable_load_balancer" {
  description = "Application Load Balancer 활성화"
  type        = bool
  default     = true
}

variable "lb_type" {
  description = "로드 밸런서 타입"
  type        = string
  default     = "application"
  validation {
    condition     = contains(["application", "network"], var.lb_type)
    error_message = "Load balancer type must be application or network."
  }
}
variable "common_tags" {
  description = "모든 리소스에 적용할 공통 태그"
  type        = map(string)
  default = {
    Terraform   = "true"
    Project     = "security-monitoring"
    Owner       = "Team2"
    Environment = "dev"
  }
}

variable "vpc_id"               { type = string }
variable "public_subnet_ids"    { type = list(string) }
variable "private_subnet_ids"   { type = list(string) }
# variable "ami_id"               { type = string }


variable "web_ami_id" {
  description = "AMI ID for web instances (ASG)"
  type        = string
}

variable "bastion_ami_id" {
  description = "AMI ID for bastion host (optional)"
  type        = string
  default     = null
}