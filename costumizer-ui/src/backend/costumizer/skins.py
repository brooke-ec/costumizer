from costumizer.config import MINESKIN_URL_BASE, MINESKIN_API_KEY, TEXTURE_URL_BASE
from costumizer.errors import NoRecordError
import costumizer.database as db
import urllib.parse as urlparse
from flask import abort
from io import BytesIO
from PIL import Image
import requests
import hashlib
import base64


def generate_hash(image: Image.Image) -> str:
    return hashlib.sha256(image.tobytes()).hexdigest()


def get_skin_from_hash(hash: str, slim: bool, resource: str) -> int:
    try:
        return db.get_skin_id(hash, slim)
    except NoRecordError:
        return generate_from_url(urlparse.urljoin(TEXTURE_URL_BASE, resource), slim, hash)


def get_skin_from_base64(encoded: str, slim: bool) -> int:
    try:
        data = base64.b64decode(encoded)
    except Exception:
        abort(400, "Encoded skin file is not valid Base64")

    try:
        image = Image.open(BytesIO(data), formats=["png"])
    except Exception:
        abort(400, "Skin file is not a valid png file")

    if image.width != 64 or image.height not in [64, 32]:
        abort(400, "Skin files must be 64x64 or 64x32 pixels in size")

    hash = generate_hash(image)

    try:
        return db.get_skin_id(hash, slim)
    except NoRecordError:
        return generate_from_upload(data, slim, hash)


def generate_from_upload(data: bytes, slim: bool, hash: str) -> int:
    try:
        response = requests.post(
            url=urlparse.urljoin(MINESKIN_URL_BASE, "upload"),
            data={"visibility": 1, "variant": "slim" if slim else "classic"},
            headers={"Authorization": f"Bearer {MINESKIN_API_KEY}"},
            files={"file": data},
        )
    except Exception:
        abort(500, "Could not connect to Mineskin API")

    return handle_mineskin_response(response, hash)


def generate_from_url(url: str, slim: bool, hash: str):
    try:
        response = requests.post(
            url=urlparse.urljoin(MINESKIN_URL_BASE, "url"),
            headers={"Authorization": f"Bearer {MINESKIN_API_KEY}"},
            json={"variant": "slim" if slim else "classic", "visibility": 1, "url": url},
        )
    except Exception:
        abort(500, "Could not connect to Mineskin API")

    return handle_mineskin_response(response, hash)


def handle_mineskin_response(response: requests.Response, hash: str) -> int:
    json = response.json()

    if response.status_code == 400:
        abort(500, "The server sent Mineskin a bad request.")
    if response.status_code == 429:
        abort(429, f"Please try again in {json['delay']} seconds")
    if response.status_code == 500:
        abort(500, "Mineskin API responded with HTTP 500")

    url: urlparse.ParseResult = urlparse.urlparse(json["data"]["texture"]["url"])
    resource = url.path.split("/")[-1]

    return db.insert_skin(
        hash,
        json["variant"] == "slim",
        resource,
        json["data"]["texture"]["value"],
        json["data"]["texture"]["signature"],
    )
