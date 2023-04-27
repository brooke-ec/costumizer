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

DB_POOL_SIZE = os.environ.get("DB_POOL_SIZE", "5")
DB_DATABASE = get_required("DB_DATABASE")
DB_PASSWORD = get_required("DB_PASSWORD")
DB_USER = get_required("DB_USER")
DB_HOST = get_required("DB_HOST")
