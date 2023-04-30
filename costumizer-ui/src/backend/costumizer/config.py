import urllib.parse as urlparse
import logging
import dotenv
import os

dotenv.load_dotenv()


def get_required(key: str) -> str:
    value = os.environ.get(key)
    if value is None:
        logging.critical("Required environment variable '%s' was omitted.", key)
        exit(1)
    return value


MINESKIN_URL_BASE = r"https://api.mineskin.org/generate/"
MINESKIN_API_KEY = get_required("MINESKIN_API_KEY")

TEXTURE_URL_BASE = r"https://textures.minecraft.net/texture/"
RENDERER_URL_BASE = get_required("RENDERER_URL_BASE")

SERVE_STATIC_FILES = os.environ.get("SERVE_STATIC_FILES", "true").lower() == "true"
STATIC_FOLDER = os.path.abspath("./static")

DB_POOL_SIZE = os.environ.get("DB_POOL_SIZE", "5")
DB_DATABASE = get_required("DB_DATABASE")
DB_PASSWORD = get_required("DB_PASSWORD")
DB_USER = get_required("DB_USER")
DB_HOST = get_required("DB_HOST")


DEFAULT_NAME = os.environ.get("DEFAULT_NAME", "Untitled")
DEFAULT_DISPLAY = os.environ.get("DEFAULT_DISPLAY", "Untitled")
DEFAULT_SLIM = os.environ.get("DEFAULT_SLIM", "false").lower() == "true"
DEFAULT_SKIN = os.environ.get(
    "DEFAULT_SKIN",
    urlparse.urljoin(
        TEXTURE_URL_BASE, "31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb"
    ),
)
