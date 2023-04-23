from costumizer.database import get_costumes, GetCostumesType
from costumizer.config import RENDERER_URL_BASE
from mysql.connector import DatabaseError
from costumizer.auth import get_uuid
from flask import Blueprint, abort
import urllib.parse as urlparse

costume = Blueprint("costume", __name__)


@costume.route("/list/")
def list_costumes():
    uuid = get_uuid()
    try:
        costumes = get_costumes(uuid)
    except DatabaseError as e:
        abort(500, f"Database Error: {e.errno}")

    def generate(c: GetCostumesType):
        params = {"hash": c["hash"], "slim": str(c["slim"]).lower(), "scale": 5}
        url = f"{RENDERER_URL_BASE}?{urlparse.urlencode(params)}"
        return {"name": c["name"], "preview": url}

    return list(map(generate, costumes)) * 19
