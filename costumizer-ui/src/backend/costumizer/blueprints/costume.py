from costumizer.config import RENDERER_URL_BASE, TEXTURE_URL_BASE
from costumizer.errors import NoRecordError
from mysql.connector import DatabaseError
from costumizer.auth import get_uuid
from flask import Blueprint, abort
import urllib.parse as urlparse
from costumizer.database import (
    get_costumes_list,
    get_costume_info,
    get_costume_existence,
    CostumesListType,
)

costume = Blueprint("costume", __name__)


@costume.get("/list/")
def list_costumes():
    uuid = get_uuid()
    try:
        costumes = get_costumes_list(uuid)
    except DatabaseError as e:
        abort(500, f"Database Error: {e.errno}")

    def generate(c: CostumesListType):
        params = {"hash": c["hash"], "slim": str(c["slim"]).lower(), "scale": 5}
        url = f"{RENDERER_URL_BASE}?{urlparse.urlencode(params)}"
        return {"name": c["name"], "preview": url}

    return list(map(generate, costumes))


@costume.get("/info/<name>")
def costume_info(name: str):
    uuid = get_uuid()
    try:
        info = get_costume_info(uuid, name)
    except DatabaseError as e:
        abort(500, f"Database Error: {e.errno}")
    except NoRecordError:
        abort(404, f"Could not find costume: {name}")
    return {
        "name": info["name"],
        "display": info["display"],
        "skin": {
            "url": urlparse.urljoin(TEXTURE_URL_BASE, info["hash"]),
            "slim": info["slim"],
        },
    }


@costume.get("/exists/<name>")
def costume_exists(name: str):
    uuid = get_uuid()
    try:
        result = get_costume_existence(uuid, name)
    except DatabaseError as e:
        abort(500, f"Database Error: {e.errno}")
    return {"exists": result}
