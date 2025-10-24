# infrastructure/terraform/modules/s3/outputs.tf


# S3 모듈 출력값 정의

# 로그 버킷 정보
output "service_bucket_id" {
  description = "서비스 버킷 ID"
  value       = aws_s3_bucket.service.id
}

output "service_bucket_arn" {
  description = "서비스 버킷 ARN"
  value       = aws_s3_bucket.service.arn
}


output "service_bucket_domain_name" {
  description = "서비스 버킷 도메인 이름"
  value       = aws_s3_bucket.service.bucket_domain_name
}

output "service_bucket_regional_domain_name" {
  description = "서비스 버킷 리전별 도메인 이름"
  value       = aws_s3_bucket.service.bucket_regional_domain_name
}



# KMS 키 정보
output "kms_key_id" {
  description = "S3 암호화 KMS 키 ID"
  value       = var.create_kms_key ? aws_kms_key.s3[0].key_id : var.kms_key_id
}

output "kms_key_arn" {
  description = "S3 암호화 KMS 키 ARN"
  value       = var.create_kms_key ? aws_kms_key.s3[0].arn : null
}

output "kms_alias_name" {
  description = "S3 KMS 키 별칭"
  value       = var.create_kms_key ? aws_kms_alias.s3[0].name : null
}


# CloudTrail용 설정 정보
output "cloudtrail_s3_config" {
  description = "CloudTrail S3 설정 정보"
  value = {
    bucket_name = aws_s3_bucket.service.id
    prefix      = "cloudtrail"
    kms_key_id  = var.create_kms_key ? aws_kms_key.s3[0].arn : var.kms_key_id
  }
}


# AWS Config용 설정 정보
output "aws_config_s3_config" {
  description = "AWS Config S3 설정 정보"
  value = {
    bucket_name = aws_s3_bucket.service.id
    prefix      = "aws-config"
    kms_key_id  = var.create_kms_key ? aws_kms_key.s3[0].arn : var.kms_key_id
  }
}


# 라이프사이클 정책 정보
output "lifecycle_policies" {
  description = "적용된 라이프사이클 정책 정보"
  value = {
    logs_retention_days              = var.log_retention_days
    backup_retention_days           = var.backup_retention_days
    transition_to_ia_days           = var.transition_to_ia_days
    transition_to_glacier_days      = var.transition_to_glacier_days
    transition_to_deep_archive_days = var.transition_to_deep_archive_days
  }
}



# 보안 설정 정보
output "security_configuration" {
  description = "S3 보안 설정 정보"
  value = {
    encryption_enabled        = true
    versioning_enabled       = var.enable_versioning
    public_access_blocked    = true
    access_logging_enabled   = var.enable_access_logging
    mfa_delete_enabled       = var.enable_mfa_delete
    object_lock_enabled      = var.enable_object_lock
  }
}




# 모니터링 설정 정보

output "monitoring_configuration" {
  description = "S3 모니터링 설정 정보"
  value = {
    cloudwatch_events_enabled    = var.enable_cloudwatch_events
    cloudwatch_monitoring_enabled = var.enable_cloudwatch_monitoring
    access_logging_enabled       = var.enable_access_logging
    inventory_enabled           = var.enable_inventory
    analytics_enabled           = var.enable_analytics
  }
}



# 성능 설정 정보
output "performance_configuration" {
  description = "S3 성능 설정 정보"
  value = {
    transfer_acceleration_enabled = var.enable_transfer_acceleration
    intelligent_tiering_enabled   = var.enable_intelligent_tiering
    multipart_threshold_mb       = var.multipart_upload_threshold
    multipart_chunk_size_mb      = var.multipart_upload_chunk_size
  }
}


# CORS 설정 정보
output "cors_configuration" {
  description = "CORS 설정 정보"
  value = {
    enabled = var.enable_cors
    rules   = var.cors_rules
  }
}



# 웹사이트 호스팅 정보
output "website_configuration" {
  description = "웹사이트 호스팅 설정 정보"
  value = var.enable_website_hosting ? {
    enabled        = true
    index_document = var.index_document
    error_document = var.error_document
  } : {
    enabled = false
  }
}


# 복제 설정 정보
output "replication_configuration" {
  description = "교차 리전 복제 설정 정보"
  value = {
    enabled                = var.enable_cross_region_replication
    destination_bucket_arn = var.replication_destination_bucket
    replication_role_arn   = var.replication_role_arn
  }
}


# 컴플라이언스 정보
output "compliance_info" {
  description = "컴플라이언스 관련 정보"
  value = {
    data_classification      = var.data_classification
    compliance_requirements  = var.compliance_requirements
    retention_policy_applied = true
    encryption_at_rest      = true
    access_audit_enabled    = var.enable_access_logging
    isms_compliant          = var.log_retention_days >= 365
  }
}


# 비용 최적화 정보
output "cost_optimization_info" {
  description = "비용 최적화 설정 정보"
  value = {
    intelligent_tiering_enabled = var.enable_intelligent_tiering
    lifecycle_transitions_configured = true
    requester_pays_enabled = var.enable_requester_pays
    storage_class_analysis_enabled = var.enable_analytics
  }
}


# 접근 경로 정보
output "s3_access_paths" {
  description = "S3 버킷 접근 경로 정보"
  value = {
    service_bucket = {
      console_url = "https://s3.console.aws.amazon.com/s3/buckets/${aws_s3_bucket.service.id}"
      s3_uri      = "s3://${aws_s3_bucket.service.id}"
      https_url   = "https://${aws_s3_bucket.service.bucket_domain_name}"
    }
  }
}
