# infrastructure/terraform/modules/s3/main.tf


# S3 버킷 구성을 위한 Terraform 모듈


# 데이터 소스
data "aws_caller_identity" "current" {}
data "aws_region" "current" {}


# KMS 키 (S3 암호화용)
resource "aws_kms_key" "s3" {
  count                   = var.create_kms_key ? 1 : 0
  description             = "S3 Bucket Encryption Key - ${var.project_name}"
  deletion_window_in_days = var.kms_deletion_window
  tags = merge(var.common_tags, {
    Name = "${var.project_name}-s3-kms-key"
    Use  = "S3 Bucket Encryption"
  })

  lifecycle {
    ignore_changes = [tags_all]
  }
}

resource "aws_kms_alias" "s3" {
  count         = var.create_kms_key ? 1 : 0
  name          = "alias/${var.project_name}-s3-test"
  target_key_id = aws_kms_key.s3[0].key_id
}



# S3 버킷
resource "aws_s3_bucket" "service" {
  bucket        = "${var.project_name}-service-${var.environment}-${random_id.bucket_suffix.hex}"
  force_destroy = var.force_destroy
  tags = merge(var.common_tags, {
    Name    = "${var.project_name}-service-bucket"
    Purpose = "service"
    Type    = "service s3"
  })
  lifecycle {
    ignore_changes = [tags_all]
  }
}




# 랜덤 ID (버킷 이름 충돌 방지)
resource "random_id" "bucket_suffix" {
  byte_length = 4
}



# S3 버킷 암호화 설정 (ISMS-P 컴플라이언스)
resource "aws_s3_bucket_server_side_encryption_configuration" "service" {
  bucket = aws_s3_bucket.service.id

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = var.create_kms_key ? aws_kms_key.s3[0].arn : var.kms_key_id
      sse_algorithm     = "aws:kms"
    }
    bucket_key_enabled = true
  }
}




# S3 버킷 버전 관리
resource "aws_s3_bucket_versioning" "service" {
  bucket = aws_s3_bucket.service.id
  versioning_configuration {
    status = var.enable_versioning ? "Enabled" : "Disabled"
  }
}


# S3 버킷 퍼블릭 액세스 차단 (보안 강화)
resource "aws_s3_bucket_public_access_block" "service" {
  bucket = aws_s3_bucket.service.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}



# S3 버킷 라이프사이클 정책

resource "aws_s3_bucket_lifecycle_configuration" "service" {
  bucket = aws_s3_bucket.service.id
  rule {
    id     = "service_lifecycle"
    status = "Enabled"

    # 현재 버전 라이프사이클
    transition {
      days          = var.transition_to_ia_days
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = var.transition_to_glacier_days
      storage_class = "GLACIER"
    }

    transition {
      days          = var.transition_to_deep_archive_days
      storage_class = "DEEP_ARCHIVE"
    }

    expiration {
      days = var.log_retention_days
    }

    # 이전 버전 관리
    noncurrent_version_transition {
      noncurrent_days = 30
      storage_class   = "STANDARD_IA"
    }


    noncurrent_version_transition {
      noncurrent_days = 60
      storage_class   = "GLACIER"
    }

    noncurrent_version_expiration {
      noncurrent_days = 90
    }

    # 불완전한 멀티파트 업로드 정리
    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }
  }
}



# AWS Config를 위한 버킷 정책
resource "aws_s3_bucket_policy" "config_policy" {
  bucket = aws_s3_bucket.service.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AWSConfigBucketPermissionsCheck"
        Effect = "Allow"
        Principal = {
          Service = "config.amazonaws.com"
        }
        Action   = "s3:GetBucketAcl"
        Resource = aws_s3_bucket.service.arn
      },
      {
        Sid    = "AWSConfigBucketExistenceCheck"
        Effect = "Allow"
        Principal = {
          Service = "config.amazonaws.com"
        }
        Action   = "s3:ListBucket"
        Resource = aws_s3_bucket.service.arn
      },
      {
        Sid    = "AWSConfigBucketDelivery"
        Effect = "Allow"
        Principal = {
          Service = "config.amazonaws.com"
        }
        Action   = "s3:PutObject"
        Resource = "${aws_s3_bucket.service.arn}/aws-config/*"
        Condition = {
          StringEquals = {
            "s3:x-amz-acl" = "bucket-owner-full-control"
          }
        }
      }
    ]
  })
}
