import subprocess
import shutil
import signal
import os

package_manager: str | None = (
    shutil.which("pnpm") if shutil.which("pnpm") else shutil.which("npm")
)
path_separator = ";" if os.name == "nt" else ":"

if package_manager is None:
    raise FileNotFoundError("Could not find npm")


def dev():
    flask_env = os.environ.copy()
    vite = subprocess.Popen([package_manager, "exec", "vite"], shell=True)
    flask_env["PYTHONPATH"] = (
        os.path.abspath("./src/backend") + path_separator + flask_env.get("PYTHONPATH", "")
    )
    flask = subprocess.Popen(
        [
            str(shutil.which("flask")),
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
