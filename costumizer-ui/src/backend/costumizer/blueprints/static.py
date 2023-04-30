from flask import Blueprint, request, send_file
import costumizer.config as config
import os

static = Blueprint("static", __name__)


@static.before_app_request
def serve_static():
    if request.path.startswith("/api"):
        return
    path = os.path.join(config.STATIC_FOLDER, request.path[1:])
    if not os.path.exists(path) or os.path.isdir(path):
        path = os.path.join(config.STATIC_FOLDER, "index.html")
    return send_file(path, max_age=31536000)
