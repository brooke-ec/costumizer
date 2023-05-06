# Costumizer

Costumizer is a plugin that allows players to customize their skin and username, powered by [Mineskin](https://mineskin.org/).
It includes a web UI for uploading skins and managing your costume library.

## Setup

Costumizer is made up of two components, the web interface and the plugin itself.

To build the plugin file, navigate to the [costumizer-ui](https://github.com/NimajnebEC/costumizer/tree/main/costumizer-plugin) subfolder and run the `build` gradle task.

The costumizer web UI is designed to be run in a docker container. To build the docker image, first you must install [python poetry](https://python-poetry.org/docs/#installation). Once installed, navigate to the [costumizer-ui](https://github.com/NimajnebEC/costumizer/tree/main/costumizer-ui) subfolder and run the `poetry run build` command.
This will build a new docker image under the name `costumizer-ui:latest`.
