from flask import Blueprint, Response, stream_with_context
import urllib.parse as urlparse
import requests

TEXTURE_BASE = r"https://textures.minecraft.net/texture/"

proxy = Blueprint("proxy", __name__)


@proxy.get("/texture/<hash>/")
def texture(hash: str):
    url = urlparse.urljoin(TEXTURE_BASE, hash)
    try:
        request = requests.get(url, stream=True)
        response = Response(
            stream_with_context(request.iter_content()),
            content_type=request.headers.get("content-type"),
            status=request.status_code,
        )
        response.cache_control.immutable = True
        response.cache_control.max_age = 86400
        response.cache_control.public = True
        return response
    except (requests.ConnectionError, requests.Timeout):
        return Response(status=500)
