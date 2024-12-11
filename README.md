# Klink

## Building
Project uses `make` to execute its build process for all modules.

### Linux / MacOS
Simply run `make build` in project root.

### Windows
There are multiple options to run `make` on Windows.

- Use GitBash

  Open GitBash in the project root and run `make build`.
  If you wish to use the integrated IntelliJ terminal to run builds, make sure to change the default shell for it. Check how to do that [here](https://www.jetbrains.com/help/idea/settings-tools-terminal.html)
  Note that this will have to be done per project.

- Install `make` using a package manager.
    - Install chocolatey -> go [here](https://chocolatey.org/install)
    - Run `choco install make`
    - Run `make build`
    
    This option is favored if you wish to use the integrated IntelliJ terminal.


## Local Development
Services and clients should be started as is. 

All modules have an IntelliJ run configuration. Use them.

In all cases, use the local docker compose script to start the database container.
```sh
make run-local
make stop-local
```

## Testing before pushing to Prod
Given that we don't have a server to use as a DEV/QA environment, a prod-like docker compose stack has been created which should effectively act the same way the production stack does.

Start the stack with `make run-prodlike`. This will spin up all containers and migrations.

- Client (web) is accessible at `localhost:3000`
- Spring backend (rest) is accessible at `localhost:3000/api`
- Ktor backend (realtime) is accessible at `localhost:3000/ws`

These routes are per `nginx` configuration located in `klink-web/nginx.conf`.

This way the application stack can be tested locally as if it were running in a production environment.

To view db contents, use the IntelliJ integrated DB tool.
