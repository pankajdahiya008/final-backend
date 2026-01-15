# ===============================
# Azure Core
# ===============================
variable "location" {
  description = "Azure region"
  type        = string
}

variable "rg_name" {
  description = "Resource group name"
  type        = string
}

variable "acr_name" {
  description = "Azure Container Registry name"
  type        = string
}

# ===============================
# Container
# ===============================
variable "container_image" {
  description = "Container image name with tag"
  type        = string
}

# ===============================
# Azure SQL
# ===============================
variable "sql_admin" {
  description = "SQL admin username"
  type        = string
}

variable "sql_password" {
  description = "SQL admin password"
  type        = string
  sensitive   = true
}

# ===============================
# Spring / App Secrets
# ===============================
variable "razorpay_api_key" {
  type      = string
  sensitive = true
}

variable "razorpay_api_secret" {
  type      = string
  sensitive = true
}

variable "stripe_api_key" {
  type      = string
  sensitive = true
}

variable "mail_username" {
  type = string
}

variable "mail_password" {
  type      = string
  sensitive = true
}

variable "gemini_api_key" {
  type      = string
  sensitive = true
}