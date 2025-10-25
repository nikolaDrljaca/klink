# Klink

Manage and share collections of links (bookmarks) in a real-time environment. The service does not
require a user account and is offline-first. 

Keep collections offline only or share them with friends with read-only or read-write permissions.


## Building
Project uses `maven` to execute its build process for all modules.

Simply run `mvn clean install` in project root.

## Local Development
Services and clients can be started in a few ways:
* With the included IntelliJ run configurations.
* With the following commands
    * `mvn spring-boot:run -Dspring-boot.run.profiles=local -DskipTests` from the `klink-rest` directory
    * `npm run dev` from the `klink-web` directory

In all cases, use the local docker compose script to start the database container.
```sh
make run-local
make stop-local
```

Alternatively, you can use `make serve` to start the database container and Spring app.

## Testing before pushing to Prod
Given that we don't have a server to use as a DEV/QA environment, a prod-like docker compose stack has been created which should effectively act the same way the production stack does.

Start the stack with `make run-prodlike`. This will spin up all containers and migrations.

- Client (web) is accessible at `localhost:3000`
- Spring backend (rest) is accessible at `localhost:3000/api`

These routes are per `nginx` configuration located in `klink-web/nginx.conf`.

This way the application stack can be tested locally as if it were running in a production environment.

Stop the stack with `make stop-prodlike`.

To view db contents, use the IntelliJ integrated DB tool or any other DB viewer tool:
- [PGAdmin](https://www.pgadmin.org/)
- `psql` - `docker exec -it <postgres-container> sh` into `psql klinkdb -U user`

## Klink Extension

To support the application workflow, a chrome extension is maintained [here](https://github.com/nikolaDrljaca/klink-ext).

