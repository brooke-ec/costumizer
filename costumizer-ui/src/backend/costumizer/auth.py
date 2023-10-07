import jwt
from costumizer.config import COSTUMIZER_SECRET
from flask import abort, current_app, request


def get_secret() -> str:
    return COSTUMIZER_SECRET


def ensure_authenticated() -> None:
    get_uuid()


def get_uuid() -> str:
    header = request.headers.get("Authorization")
    if header is None:
        abort(401, "No authorization header present.")
    if header[:6].lower() != "bearer":
        abort(401, "Authorization is not bearer")

    token = header[7:].strip()
    try:
        payload = jwt.decode(
            token,
            current_app.config["TOKEN_SECRET"],
            algorithms=["HS256"],
        )
    except jwt.InvalidSignatureError:
        abort(401, "Provided token has invalid signature.")
    except jwt.DecodeError:
        abort(401, "Provided token is invalid.")
    return payload["uuid"]
