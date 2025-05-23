#!/bin/bash

# Nome dos recursos
RESOURCE_GROUP="safeyard-rg"
PLAN_NAME="safeyard-plan"
APP_NAME="safeyard-api-thamires"
LOCATION="brazilsouth"
DOCKER_IMAGE="thamiresrc/safeyard-api"
PORT=8080

echo "🔐 Verificando login no Azure..."
az account show > /dev/null 2>&1 || az login

echo "📁 Criando grupo de recursos: $RESOURCE_GROUP"
az group create --name $RESOURCE_GROUP --location $LOCATION

echo "🛠️ Criando App Service Plan: $PLAN_NAME"
az appservice plan create \
  --name $PLAN_NAME \
  --resource-group $RESOURCE_GROUP \
  --sku B1 \
  --is-linux

echo "🌐 Criando Web App: $APP_NAME"
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $PLAN_NAME \
  --name $APP_NAME \
  --deployment-container-image-name $DOCKER_IMAGE

echo "⚙️ Configurando porta do container para $PORT"
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings WEBSITES_PORT=$PORT

echo "🚀 Aplicação publicada com sucesso!"
echo "🌍 Acesse sua API em: https://$APP_NAME.azurewebsites.net"
