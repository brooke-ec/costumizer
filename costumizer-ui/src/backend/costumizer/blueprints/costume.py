from costumizer.config import RENDERER_URL_BASE, TEXTURE_URL_BASE
from costumizer.utils import get_parameter, get_body
from costumizer.errors import NoRecordError
from costumizer.auth import get_uuid
from flask import Blueprint, abort
import costumizer.database as db
import urllib.parse as urlparse
from typing import TypedDict

costume = Blueprint("costume", __name__)


@costume.get("/list")
def list_costumes():
    uuid = get_uuid()
    costumes = db.get_costumes_list(uuid)

    def generate(c: db.CostumesListType):
        params = {"hash": c["hash"], "slim": str(c["slim"]).lower(), "scale": 5}
        url = f"{RENDERER_URL_BASE}?{urlparse.urlencode(params)}"
        return {"name": c["name"], "preview": url}

    return list(map(generate, costumes))


@costume.get("/info")
def costume_info():
    name = get_parameter("name")
    uuid = get_uuid()
    try:
        info = db.get_costume_info(uuid, name)
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


@costume.get("/exists")
def costume_exists():
    name = get_parameter("name")
    uuid = get_uuid()
    result = db.get_costume_existence(uuid, name)
    return {"exists": result}


class SaveBodyType(TypedDict):
    skin: str | None
    display: str
    slim: bool
    name: str


@costume.post("/update")
def update_costume():
    name = get_parameter("name")
    body = get_body(SaveBodyType)
    uuid = get_uuid()

    try:
        skin_info = db.get_costume_skin(uuid, name)
    except NoRecordError:
        abort(404, f"Could not find costume: {name}")

    db.update_costume(uuid, name, body["name"], skin_info["hash"], body["display"])
    return {"successful": True}
