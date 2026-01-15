#!/bin/bash
set -e

SUB="1dce2c73-ee07-4b01-a0d9-210ced8a444d"
RG="rg-aca-sql"

# -------------------------
# Resource Group
# -------------------------
terraform import azurerm_resource_group.rg \
/subscriptions/$SUB/resourceGroups/$RG

# -------------------------
# Virtual Network
# -------------------------
terraform import azurerm_virtual_network.vnet \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/virtualNetworks/vnet-aca

# -------------------------
# Subnets
# -------------------------
terraform import azurerm_subnet.aca \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/virtualNetworks/vnet-aca/subnets/snet-containerapps

terraform import azurerm_subnet.db \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/virtualNetworks/vnet-aca/subnets/snet-db

# -------------------------
# Azure Container Registry
# -------------------------
terraform import azurerm_container_registry.acr \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.ContainerRegistry/registries/acracademodemo123

# -------------------------
# Azure SQL Server & Database
# -------------------------
terraform import azurerm_mssql_server.sql \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Sql/servers/sql-aca-demo-01

terraform import azurerm_mssql_database.db \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Sql/servers/sql-aca-demo-01/databases/ecommerce

# -------------------------
# Private DNS Zone
# -------------------------
terraform import azurerm_private_dns_zone.sql \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/privateDnsZones/privatelink.database.windows.net

# -------------------------
# DNS VNet Link
# -------------------------
terraform import azurerm_private_dns_zone_virtual_network_link.sql \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/privateDnsZones/privatelink.database.windows.net/virtualNetworkLinks/sql-dns-link

# -------------------------
# Private Endpoint
# -------------------------
terraform import azurerm_private_endpoint.sql \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Network/privateEndpoints/pe-sql

# -------------------------
# Container App Environment
# -------------------------
terraform import azurerm_container_app_environment.env \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Web/containerAppEnvironments/aca-env

# -------------------------
# Container App
# -------------------------
terraform import azurerm_container_app.app \
/subscriptions/$SUB/resourceGroups/$RG/providers/Microsoft.Web/containerApps/spring-api