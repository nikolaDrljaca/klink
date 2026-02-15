# List available recipes
default:
    @just --list

# Start(up) either the local or prodlike docker stack
[group('dev')]
[arg('env', pattern='local|prodlike', help='Start the {local} or {prodlike} docker stack')]
up env:
    @echo 'Starting {{env}} stack.'
    docker compose -f docker-compose.{{env}}.yaml up --build -d

# Stop(down) either the local or prodlike docker stack
[group('dev')]
[arg('env', pattern='local|prodlike', help='Shut down the {local} or {prodlike} docker stack')]
down env:
    @echo 'Stopping {{env}} stack.'
    docker compose -f docker-compose.{{env}}.yaml down

# Start everything for local client development
[group('dev')]
[working-directory: 'klink-rest']
serve:
    docker compose -f ../docker-compose.local.yaml up --build -d
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=local -DskipTests
