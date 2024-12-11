# Builds
web-build:
	cd klink-web && npm install && npm run openapi-gen && npm run build

rest-build:
	cd klink-rest && ./mvnw clean install

realtime-build:
	cd klink-realtime && ./gradlew clean build

build: web-build rest-build realtime-build

# Run Configurations
run-local:
	docker compose -f docker-compose.local.yaml up --build -d
stop-local:
	docker compose -f docker-compose.local.yaml down
run-prodlike:
	docker compose -f docker-compose.prodlike.yaml up --build -d
stop-prodlike:
	docker compose -f docker-compose.prodlike.yaml down
