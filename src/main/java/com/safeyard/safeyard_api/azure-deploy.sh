# azure-deploy.sh

# 1. Login no Azure
az login

# 2. Criar grupo de recursos
az group create --name safeyard-rg --location brazilsouth

# 3. Registrar o provedor (executado só uma vez)
az provider register --namespace Microsoft.Web

# 4. Criar App Service Plan
az appservice plan create --name safeyard-plan --resource-group safeyard-rg --sku B1 --is-linux

# 5. Criar Web App com imagem do DockerHub
az webapp create \
  --resource-group safeyard-rg \
  --plan safeyard-plan \
  --name safeyard-api-thamires \
  --deployment-container-image-name thamiresrc/safeyard-api

# 6. Definir porta do container (importante para Spring Boot)
az webapp config appsettings set \
  --resource-group safeyard-rg \
  --name safeyard-api-thamires \
  --settings WEBSITES_PORT=8080
