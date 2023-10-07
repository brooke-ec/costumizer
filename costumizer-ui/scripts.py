import os
import shutil
import signal
import subprocess

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
