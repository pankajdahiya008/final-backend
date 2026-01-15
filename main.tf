# main.tf

terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.100"
    }
  }
}

provider "azurerm" {
  features {}
}

# -------------------------
# Resource Group
# -------------------------
resource "azurerm_resource_group" "rg" {
  name     = var.rg_name
  location = var.location
}

# -------------------------
# Virtual Network
# -------------------------
resource "azurerm_virtual_network" "vnet" {
  name                = "vnet-aca"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  address_space       = ["10.0.0.0/16"]
}

# -------------------------
# Subnets
# -------------------------
resource "azurerm_subnet" "aca" {
  name                 = "snet-containerapps"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.0.0/23"]
}

resource "azurerm_subnet" "db" {
  name                 = "snet-db"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.4.0/24"]
}

# -------------------------
# Azure Container Registry
# -------------------------
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = false
}

# -------------------------
# Azure SQL Server and Database
# -------------------------
resource "azurerm_mssql_server" "sql" {
  name                          = "sql-aca-demo-01"
  resource_group_name           = azurerm_resource_group.rg.name
  location                      = azurerm_resource_group.rg.location
  version                       = "12.0"
  administrator_login           = var.sql_admin
  administrator_login_password  = var.sql_password
  public_network_access_enabled = false
}

resource "azurerm_mssql_database" "db" {
  name      = "ecommerce"
  server_id = azurerm_mssql_server.sql.id
  sku_name  = "Basic"
}

# -------------------------
# Private DNS for SQL
# -------------------------
resource "azurerm_private_dns_zone" "sql" {
  name                = "privatelink.database.windows.net"
  resource_group_name = azurerm_resource_group.rg.name
}

resource "azurerm_private_dns_zone_virtual_network_link" "sql" {
  name                  = "sql-dns-link"
  resource_group_name   = azurerm_resource_group.rg.name
  private_dns_zone_name = azurerm_private_dns_zone.sql.name
  virtual_network_id    = azurerm_virtual_network.vnet.id
}

# -------------------------
# Private Endpoint for SQL
# -------------------------
resource "azurerm_private_endpoint" "sql" {
  name                = "pe-sql"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  subnet_id           = azurerm_subnet.db.id

  private_service_connection {
    name                           = "sql-conn"
    private_connection_resource_id = azurerm_mssql_server.sql.id
    subresource_names              = ["sqlServer"]
    is_manual_connection           = false
  }

  private_dns_zone_group {
    name                 = "sql-dns"
    private_dns_zone_ids = [azurerm_private_dns_zone.sql.id]
  }
}

# -------------------------
# Container Apps Environment
# -------------------------
resource "azurerm_container_app_environment" "env" {
  name                     = "aca-env"
  location                 = azurerm_resource_group.rg.location
  resource_group_name      = azurerm_resource_group.rg.name
  infrastructure_subnet_id = azurerm_subnet.aca.id
}

# -------------------------
# Container App
# -------------------------
resource "azurerm_container_app" "app" {
  name                         = "spring-api"
  resource_group_name          = azurerm_resource_group.rg.name
  container_app_environment_id = azurerm_container_app_environment.env.id
  revision_mode                = "Single"

  identity {
    type = "SystemAssigned"
  }

  # -------------------------
  # Secrets from variables
  # -------------------------
  secret {
    name  = "db-password"
    value = var.sql_password
  }

  secret {
    name  = "razorpay-api-key"
    value = var.razorpay_api_key
  }

  secret {
    name  = "razorpay-api-secret"
    value = var.razorpay_api_secret
  }

  secret {
    name  = "stripe-api-key"
    value = var.stripe_api_key
  }

  secret {
    name  = "mail-username"
    value = var.mail_username
  }

  secret {
    name  = "mail-password"
    value = var.mail_password
  }

  secret {
    name  = "gemini-api-key"
    value = var.gemini_api_key
  }

  # -------------------------
  # Container Template
  # -------------------------
  template {
    min_replicas = 1
    max_replicas = 2

    container {
      name   = "app"
      image  = "${azurerm_container_registry.acr.login_server}/springboot-app:v1"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:sqlserver://${azurerm_mssql_server.sql.name}.database.windows.net:1433;database=${azurerm_mssql_database.db.name};encrypt=true;trustServerCertificate=false;loginTimeout=30;"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.sql_admin
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name        = "RAZORPAY_API_KEY"
        secret_name = "razorpay-api-key"
      }

      env {
        name        = "RAZORPAY_API_SECRET"
        secret_name = "razorpay-api-secret"
      }

      env {
        name        = "STRIPE_API_KEY"
        secret_name = "stripe-api-key"
      }

      env {
        name        = "MAIL_USERNAME"
        secret_name = "mail-username"
      }

      env {
        name        = "MAIL_PASSWORD"
        secret_name = "mail-password"
      }

      env {
        name        = "GEMINI_API_KEY"
        secret_name = "gemini-api-key"
      }
    }
  }

  # -------------------------
  # Container Registry
  # -------------------------
  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = "system"
  }

  # -------------------------
  # Ingress
  # -------------------------
  ingress {
    external_enabled = true
    target_port      = 8080

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }
}

# -------------------------
# ACA role assignment for ACR pull
# -------------------------
resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_container_app.app.identity[0].principal_id
}

# -------------------------
# Outputs
# -------------------------
output "app_url" {
  value = azurerm_container_app.app.ingress[0].fqdn
}