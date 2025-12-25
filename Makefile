# Run Configurations
local-up:
	docker compose -f docker-compose.local.yaml up --build -d
local-down:
	docker compose -f docker-compose.local.yaml down
prodlike-up:
	docker compose -f docker-compose.prodlike.yaml up --build -d
prodlike-down:
	docker compose -f docker-compose.prodlike.yaml down
serve:
	docker compose -f docker-compose.local.yaml up --build -d
	cd klink-rest && mvn spring-boot:run -Dspring-boot.run.profiles=local -DskipTests
