from costumizer.config import TEXTURE_URL_BASE
from costumizer.auth import get_uuid
from flask import Blueprint, abort
import urllib.parse as urlparse
from typing import Any
import requests
import base64
import json

DEFAULT_CLASSIC = r"31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb"
DEFAULT_SLIM = r"46acd06e8483b176e8ea39fc12fe105eb3a2a4970f5100057e9d84d4b60bdfa7"
PROFILE_BASE = r"https://sessionserver.mojang.com/session/minecraft/profile/"

user = Blueprint("user", __name__)


@user.get("/info")
def user_info():
    uuid = get_uuid()
    url = urlparse.urljoin(PROFILE_BASE, uuid)
    try:
        request = requests.get(url)
    except (requests.ConnectionError, requests.Timeout):
        parsed = urlparse.urlparse(url)
        abort(500, f"Could not connect to {parsed.netloc}")
    if request.status_code == 404:
        abort(500, f"Could not locate {url}")
    if request.status_code == 204:
        abort(401, f"No profile {uuid}")

    data = request.json()
    if request.status_code == 400:
        abort(500, data["errorMessage"])

    return {"id": data["id"], "name": data["name"], "skin": get_skin(data["properties"])}


def get_skin(properties: list[dict[str, Any]]) -> str:
    property = next((p for p in properties if p["name"] == "textures"), None)
    if property is None:
        return TEXTURE_URL_BASE + DEFAULT_CLASSIC

    textures: dict[str, dict] = json.loads(base64.b64decode(property["value"]))
    skin: dict[str, str | dict[str, str]] | None = textures["textures"].get("SKIN")
    if skin is None:
        return TEXTURE_URL_BASE + DEFAULT_CLASSIC

    model = skin.get("metadata", {}).get("model", "classic")

    if "url" not in textures["textures"]["SKIN"]:
        return TEXTURE_URL_BASE + (DEFAULT_CLASSIC if model == "classic" else DEFAULT_SLIM)

    return skin["url"]
