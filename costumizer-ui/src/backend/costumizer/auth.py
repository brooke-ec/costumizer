from flask import request, abort, current_app
import base64
import jwt
import os


def get_secret() -> str:
    if os.path.exists("costumizer.secret"):
        with open("costumizer.secret", "r") as f:
            return f.read()

    token = base64.b64encode(os.urandom(32)).decode()
    with open("costumizer.secret", "w") as f:
        f.write(token)
    return token


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
