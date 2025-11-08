#!/bin/bash
set -e

# ===== Configs (edite se precisar) =====
RESOURCE_GROUP="safeyard-rg"
PLAN_NAME="safeyard-plan"
APP_NAME="safeyard-api-thamires"
LOCATION="brazilsouth"
DOCKER_IMAGE="thamiresrc/safeyard-api"
PORT=8080

# Credenciais do SQL Server (use secrets/KeyVault em prod real)
SPRING_DATASOURCE_URL="jdbc:sqlserver://sql-safeyard-372b.database.windows.net:1433;database=db-safeyard;encrypt=true;trustServerCertificate=false;loginTimeout=30"
SPRING_DATASOURCE_USERNAME="sqladmin@sql-safeyard-372b"
SPRING_DATASOURCE_PASSWORD="Fiap@2tdsvms"
APP_CORS_ALLOWED_ORIGINS="http://localhost:19006"

echo "🔐 Verificando login no Azure..."
az account show > /dev/null 2>&1 || az login

echo "📁 Criando grupo de recursos: $RESOURCE_GROUP"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION"

echo "🛠️ Criando App Service Plan: $PLAN_NAME"
az appservice plan create \
  --name "$PLAN_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --sku B1 \
  --is-linux

echo "🌐 Criando Web App: $APP_NAME"
az webapp create \
  --resource-group "$RESOURCE_GROUP" \
  --plan "$PLAN_NAME" \
  --name "$APP_NAME" \
  --deployment-container-image-name "$DOCKER_IMAGE"

echo "⚙️ Configurando app settings"
az webapp config appsettings set \
  --resource-group "$RESOURCE_GROUP" \
  --name "$APP_NAME" \
  --settings \
    WEBSITES_PORT="$PORT" \
    PORT="$PORT" \
    SPRING_PROFILES_ACTIVE="prod" \
    SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
    SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
    SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
    APP_CORS_ALLOWED_ORIGINS="$APP_CORS_ALLOWED_ORIGINS"

echo "🚀 Publicado! Acesse: https://$APP_NAME.azurewebsites.net"
