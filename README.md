# Klink

## Building
Project uses `make` to execute its build process for all modules.

### Linux
Simply run `make build` in project root.

### Windows
There are multiple options to run `make` on Windows.

- Use GitBash

Open GitBash in the project root and run `make build`.
If you wish to use the integrated IntelliJ terminal to run builds, make sure to change the default shell for it. Check how to do that [here](https://www.jetbrains.com/help/idea/settings-tools-terminal.html)
Note that this will have to be done per project.

- Install `make` using a package manager
    - Install chocolatey -> go [here](https://chocolatey.org/install)
    - Run `choco install make`
    - Run `make build`

This option is favored if you wish to use the integrated IntelliJ terminal.


## Local Development
Services and clients should be started as is. 

- For `realtime` and `rest` use IntelliJ run configurations.
- For `cliet`, use `npm run dev`

In all cases, use the local docker compose script to start the database container
```sh
make run-local
make stop-local
```

To view db contents, use the IntelliJ integrated DB tool.
