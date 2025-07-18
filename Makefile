# Run Configurations
run-local:
	docker compose -f docker-compose.local.yaml up --build -d
stop-local:
	docker compose -f docker-compose.local.yaml down
run-prodlike:
	docker compose -f docker-compose.prodlike.yaml up --build -d
stop-prodlike:
	docker compose -f docker-compose.prodlike.yaml down
