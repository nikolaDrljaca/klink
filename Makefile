# Run Configurations
run-local:
	docker compose -f docker-compose.local.yaml up --build -d
stop-local:
	docker compose -f docker-compose.local.yaml down
run-prodlike:
	echo 'Running PRODLIKE stack. Building `hop-service` will take some time.'
	docker compose -f docker-compose.prodlike.yaml up --build -d
stop-prodlike:
	docker compose -f docker-compose.prodlike.yaml down
serve:
	docker compose -f docker-compose.local.yaml up --build -d
	cd klink-rest && mvn spring-boot:run -Dspring-boot.run.profiles=local -DskipTests
