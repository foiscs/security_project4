# infrastructure/terraform/modules/dynamodb/main.tf


# DynamoDB 테이블 구성을 위한 Terraform 모듈





# KMS 키 (DynamoDB 암호화용)


resource "aws_kms_key" "dynamodb" {


  count                   = var.create_kms_key ? 1 : 0


  description             = "DynamoDB Table Encryption Key - ${var.project_name}"


  deletion_window_in_days = var.kms_deletion_window





  tags = merge(var.common_tags, {


    Name = "${var.project_name}-dynamodb-kms-key"


    Use  = "DynamoDB Encryption"


  })


  lifecycle {


    ignore_changes = [tags_all]


  }


}





resource "aws_kms_alias" "dynamodb" {


  count         = var.create_kms_key ? 1 : 0


  name          = "alias/${var.project_name}-${var.table_name}-dynamodb-test"


  target_key_id = aws_kms_key.dynamodb[0].key_id


}





# 메인 DynamoDB 테이블


resource "aws_dynamodb_table" "this" {


  count = var.enabled ? 1 : 0





  name         = var.name


  billing_mode = "PAY_PER_REQUEST"





  tags = merge(var.common_tags, {


    Name = var.table_name != null ? var.table_name : "${var.project_name}-${var.environment}-table"


    Type = "DynamoDB Table"


  })





  lifecycle {


    prevent_destroy = false  


    ignore_changes = [tags_all]


  }


}





# Auto Scaling 설정 (Provisioned 모드일 때만)


resource "aws_appautoscaling_target" "dynamodb_table_read_target" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling ? 1 : 0


  max_capacity       = var.autoscaling_read_max_capacity


  min_capacity       = var.autoscaling_read_min_capacity


  resource_id        = "table/${aws_dynamodb_table.main.name}"


  scalable_dimension = "dynamodb:table:ReadCapacityUnits"


  service_namespace  = "dynamodb"


}





resource "aws_appautoscaling_target" "dynamodb_table_write_target" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling ? 1 : 0


  max_capacity       = var.autoscaling_write_max_capacity


  min_capacity       = var.autoscaling_write_min_capacity


  resource_id        = "table/${aws_dynamodb_table.main.name}"


  scalable_dimension = "dynamodb:table:WriteCapacityUnits"


  service_namespace  = "dynamodb"


}





resource "aws_appautoscaling_policy" "dynamodb_table_read_policy" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling ? 1 : 0


  name               = "DynamoDBReadCapacityUtilization:${aws_appautoscaling_target.dynamodb_table_read_target[0].resource_id}"


  policy_type        = "TargetTrackingScaling"


  resource_id        = aws_appautoscaling_target.dynamodb_table_read_target[0].resource_id


  scalable_dimension = aws_appautoscaling_target.dynamodb_table_read_target[0].scalable_dimension


  service_namespace  = aws_appautoscaling_target.dynamodb_table_read_target[0].service_namespace





  target_tracking_scaling_policy_configuration {


    predefined_metric_specification {


      predefined_metric_type = "DynamoDBReadCapacityUtilization"


    }


    target_value = var.autoscaling_read_target_value


  }


}





resource "aws_appautoscaling_policy" "dynamodb_table_write_policy" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling ? 1 : 0


  name               = "DynamoDBWriteCapacityUtilization:${aws_appautoscaling_target.dynamodb_table_write_target[0].resource_id}"


  policy_type        = "TargetTrackingScaling"


  resource_id        = aws_appautoscaling_target.dynamodb_table_write_target[0].resource_id


  scalable_dimension = aws_appautoscaling_target.dynamodb_table_write_target[0].scalable_dimension


  service_namespace  = aws_appautoscaling_target.dynamodb_table_write_target[0].service_namespace





  target_tracking_scaling_policy_configuration {


    predefined_metric_specification {


      predefined_metric_type = "DynamoDBWriteCapacityUtilization"


    }


    target_value = var.autoscaling_write_target_value


  }


}





# GSI Auto Scaling


resource "aws_appautoscaling_target" "dynamodb_gsi_read_target" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling && length(var.global_secondary_indexes) > 0 ? length(var.global_secondary_indexes) : 0


  max_capacity       = var.gsi_autoscaling_read_max_capacity


  min_capacity       = var.gsi_autoscaling_read_min_capacity


  resource_id        = "table/${aws_dynamodb_table.main.name}/index/${var.global_secondary_indexes[count.index].name}"


  scalable_dimension = "dynamodb:index:ReadCapacityUnits"


  service_namespace  = "dynamodb"


}





resource "aws_appautoscaling_target" "dynamodb_gsi_write_target" {


  count              = var.billing_mode == "PROVISIONED" && var.enable_autoscaling && length(var.global_secondary_indexes) > 0 ? length(var.global_secondary_indexes) : 0


  max_capacity       = var.gsi_autoscaling_write_max_capacity


  min_capacity       = var.gsi_autoscaling_write_min_capacity


  resource_id        = "table/${aws_dynamodb_table.main.name}/index/${var.global_secondary_indexes[count.index].name}"


  scalable_dimension = "dynamodb:index:WriteCapacityUnits"


  service_namespace  = "dynamodb"


}





# CloudWatch 알람 (성능 모니터링)


resource "aws_cloudwatch_metric_alarm" "dynamodb_read_throttled_requests" {


  count               = var.enable_cloudwatch_alarms ? 1 : 0


  alarm_name          = "${aws_dynamodb_table.main.name}-read-throttled-requests"


  comparison_operator = "GreaterThanThreshold"


  evaluation_periods  = "2"


  metric_name         = "ReadThrottledEvents"


  namespace           = "AWS/DynamoDB"


  period              = "300"


  statistic           = "Sum"


  threshold           = var.read_throttle_alarm_threshold


  alarm_description   = "This metric monitors DynamoDB read throttled requests"


  alarm_actions       = var.sns_topic_arn != null ? [var.sns_topic_arn] : []





  dimensions = {


    TableName = aws_dynamodb_table.main.name


  }





  tags = var.common_tags


}





resource "aws_cloudwatch_metric_alarm" "dynamodb_write_throttled_requests" {


  count               = var.enable_cloudwatch_alarms ? 1 : 0


  alarm_name          = "${aws_dynamodb_table.main.name}-write-throttled-requests"


  comparison_operator = "GreaterThanThreshold"


  evaluation_periods  = "2"


  metric_name         = "WriteThrottledEvents"


  namespace           = "AWS/DynamoDB"


  period              = "300"


  statistic           = "Sum"


  threshold           = var.write_throttle_alarm_threshold


  alarm_description   = "This metric monitors DynamoDB write throttled requests"


  alarm_actions       = var.sns_topic_arn != null ? [var.sns_topic_arn] : []





  dimensions = {


    TableName = aws_dynamodb_table.main.name


  }





  tags = var.common_tags


}





resource "aws_cloudwatch_metric_alarm" "dynamodb_consumed_read_capacity" {


  count               = var.enable_cloudwatch_alarms && var.billing_mode == "PROVISIONED" ? 1 : 0


  alarm_name          = "${aws_dynamodb_table.main.name}-high-read-capacity"


  comparison_operator = "GreaterThanThreshold"


  evaluation_periods  = "2"


  metric_name         = "ConsumedReadCapacityUnits"


  namespace           = "AWS/DynamoDB"


  period              = "300"


  statistic           = "Average"


  threshold           = var.read_capacity * 0.8


  alarm_description   = "This metric monitors DynamoDB consumed read capacity"


  alarm_actions       = var.sns_topic_arn != null ? [var.sns_topic_arn] : []





  dimensions = {


    TableName = aws_dynamodb_table.main.name


  }





  tags = var.common_tags


}





# DynamoDB Backup 설정(제거)





# Global Tables 설정 (선택사항)


resource "aws_dynamodb_global_table" "main" {


  count = var.enable_global_tables ? 1 : 0


  name  = aws_dynamodb_table.main.name





  dynamic "replica" {


    for_each = var.global_table_regions


    content {


      region_name = replica.value


    }


  }





  depends_on = [aws_dynamodb_table.main]


}





# DynamoDB Contributor Insights (성능 분석)


resource "aws_dynamodb_contributor_insights" "main" {


  count      = var.enable_contributor_insights ? 1 : 0


  table_name = aws_dynamodb_table.main.name


}





# DynamoDB Table Replica (다른 리전, Global Tables 대신 사용 가능)


resource "aws_dynamodb_table_replica" "main" {


  count            = var.create_replica ? 1 : 0


  global_table_arn = aws_dynamodb_table.main.arn





  kms_key_arn                = var.replica_kms_key_arn


  point_in_time_recovery     = var.replica_point_in_time_recovery


  # table_class               = var.replica_table_class





  tags = merge(var.common_tags, {


    Name = "${aws_dynamodb_table.main.name}-replica"


    Type = "DynamoDB Replica"


  })


  lifecycle {


    ignore_changes = [tags_all]


  }


}





# IAM 역할 (DynamoDB 접근용)


resource "aws_iam_role" "dynamodb_access" {


  count = var.create_access_role ? 1 : 0


  name  = "${var.project_name}-dynamodb-access-role"





  assume_role_policy = jsonencode({


    Version = "2012-10-17"


    Statement = [


      {


        Action = "sts:AssumeRole"


        Effect = "Allow"


        Principal = {


          Service = var.access_role_principals


        }


      }


    ]


  })





  tags = var.common_tags


}





resource "aws_iam_role_policy" "dynamodb_access" {


  count = var.create_access_role ? 1 : 0


  name  = "${var.project_name}-dynamodb-access-policy"


  role  = aws_iam_role.dynamodb_access[0].id





  policy = jsonencode({


    Version = "2012-10-17"


    Statement = [


      {


        Effect = "Allow"


        Action = var.access_role_permissions


        Resource = [


          aws_dynamodb_table.main.arn,


          "${aws_dynamodb_table.main.arn}/index/*",


          "${aws_dynamodb_table.main.arn}/stream/*"


        ]


      }


    ]


  })


}



