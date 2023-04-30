import subprocess
import shutil
import signal
import os

package_manager: str | None = (
    shutil.which("pnpm") if shutil.which("pnpm") else shutil.which("npm")
)
if package_manager is None:
    raise FileNotFoundError("Could not find npm")

path_separator = ";" if os.name == "nt" else ":"


def dev():
    flask_path: str | None = shutil.which("flask")
    if flask_path is None:
        raise FileNotFoundError("Could not find flask")

    flask_env = os.environ.copy()
    vite = subprocess.Popen([package_manager, "exec", "vite"], shell=True)
    flask_env["PYTHONPATH"] = (
        os.path.abspath("./src/backend") + path_separator + flask_env.get("PYTHONPATH", "")
    )
    flask = subprocess.Popen(
        [
            flask_path,
            "--app",
            "costumizer",
            "--debug",
            "run",
            "--port",
            "3001",
            "--with-threads",
        ],
        cwd=os.getcwd(),
        env=flask_env,
        shell=True,
    )
    vite.wait()
    os.kill(flask.pid, signal.CTRL_C_EVENT)


def build():
    poetry_path: str | None = shutil.which("poetry")
    if poetry_path is None:
        raise FileNotFoundError("Could not find poetry")

    docker_path: str | None = shutil.which("docker")
    if docker_path is None:
        raise FileNotFoundError("Could not find docker")

    # Vite Build
    p = subprocess.run([package_manager, "exec", "vite", "build"], shell=True)
    if p.returncode != 0:
        return

    # Get Poetry Requirements
    p = subprocess.run(
        [
            poetry_path,
            "export",
            "-f",
            "requirements.txt",
            "-o",
            "./build/requirements.txt",
            "--without-hashes",
        ],
        shell=True,
    )
    if p.returncode != 0:
        return

    shutil.copytree("./src/backend/", "./build/app", dirs_exist_ok=True)
    shutil.copy("./src/Dockerfile", "./build/")

    p = subprocess.run(
        [
            docker_path,
            "build",
            "--pull",
            "--rm",
            "-t",
            "costumizer-ui:latest",
            ".",
        ],
        cwd="./build",
        shell=True,
    )
    if p.returncode != 0:
        return

    print("costumizer-ui built!")
