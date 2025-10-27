# infrastructure/terraform/modules/ec2/outputs.tf



output "alb_configuration" {
  description = "ALB 설정 정보"
  value = var.enable_load_balancer ? {
    alb_arn           = aws_lb.main[0].arn
    alb_dns_name      = aws_lb.main[0].dns_name
    alb_zone_id       = aws_lb.main[0].zone_id
    target_group_arn  = aws_lb_target_group.web.arn           
    listener_arn      = aws_lb_listener.web[0].arn # ← 리스너 라벨 web
    security_group_id = aws_security_group.alb[0].id
  } : null
}

output "bastion_security_group_id" {
  description = "Bastion 보안그룹 ID"
  value       = aws_security_group.bastion.id
}

output "web_security_group_id" {
  description = "웹 인스턴스 SG ID"
  value       = aws_security_group.web.id
}
