deploy:
	az account show
	az acr login --name acracademodemo123
	docker buildx build \
		--platform linux/amd64 \
		-t acracademodemo123.azurecr.io/springboot-app:v2 \
		--push .
	az containerapp update \
		--name spring-api \
		--resource-group rg-aca-sql \
		--image acracademodemo123.azurecr.io/springboot-app:v2