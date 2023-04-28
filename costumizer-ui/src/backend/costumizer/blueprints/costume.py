from costumizer.skins import get_skin_from_base64, get_skin_from_hash, get_skin_from_url
from costumizer.utils import get_parameter, get_body
from costumizer.errors import NoRecordError
from costumizer.auth import get_uuid
from flask import Blueprint, abort
import costumizer.config as config
import costumizer.database as db
import urllib.parse as urlparse
from typing import TypedDict
import re

costume = Blueprint("costume", __name__)


class SaveBodyType(TypedDict):
    skin: str | None
    display: str
    slim: bool
    name: str


RE_NAME = re.compile(r"^[\w\d_]{1,32}$")
RE_DISPLAY = re.compile(r"^[\w\d_]{3,16}$")


@costume.get("/list")
def list_costumes():
    uuid = get_uuid()
    costumes = db.get_costumes_list(uuid)

    def generate(c: db.CostumesListType):
        params = {"hash": c["resource"], "slim": str(c["slim"]).lower(), "scale": 5}
        url = f"{config.RENDERER_URL_BASE}?{urlparse.urlencode(params)}"
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
            "url": urlparse.urljoin(config.TEXTURE_URL_BASE, info["resource"]),
            "slim": info["slim"],
        },
    }


@costume.get("/exists")
def costume_exists():
    name = get_parameter("name")
    uuid = get_uuid()
    print(name)
    result = db.get_costume_existence(uuid, name)
    return {"exists": result}


@costume.post("/update")
def update_costume():
    name = get_parameter("name")
    body = get_body(SaveBodyType)
    uuid = get_uuid()

    if RE_NAME.match(body["name"]) is None:
        abort(400, "Name field is invalid.")
    if RE_DISPLAY.match(body["display"]) is None:
        abort(400, "Display field is invalid.")

    try:
        skin_info = db.get_costume_skin(uuid, name)
    except NoRecordError:
        abort(404, f"Could not find costume: {name}")

    if body["skin"] is None:
        if body["slim"] == skin_info["slim"]:
            skin = skin_info["id"]
        else:
            skin = get_skin_from_hash(
                skin_info["hash"], body["slim"], skin_info["resource"]
            )
    else:
        skin = get_skin_from_base64(body["skin"], body["slim"])

    db.update_costume(uuid, name, body["name"], skin, body["display"])
    return {"successful": True}


@costume.post("/create")
def create_costume():
    body = get_body(SaveBodyType)
    uuid = get_uuid()

    if RE_NAME.match(body["name"]) is None:
        abort(400, "Name field is invalid.")
    if RE_DISPLAY.match(body["display"]) is None:
        abort(400, "Display field is invalid.")

    if body["skin"] is None:
        skin = get_skin_from_url(config.DEFAULT_SKIN, body["slim"])
    else:
        skin = get_skin_from_base64(body["skin"], body["slim"])

    db.insert_costume(uuid, body["name"], skin, body["display"])
    return {"successful": True}


@costume.post("/delete")
def delete_costume():
    name = get_parameter("name")
    uuid = get_uuid()
    deleted = db.delete_costume(name, uuid)
    if not deleted:
        abort(404, f"Could not find costume: {name}")
    return {"successful": True}


@costume.get("/defaults")
def get_defaults():
    return {
        "name": config.DEFAULT_NAME,
        "display": config.DEFAULT_DISPLAY,
        "skin": {
            "url": config.DEFAULT_SKIN,
            "slim": config.DEFAULT_SLIM,
        },
    }
