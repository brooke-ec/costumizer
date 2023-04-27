from costumizer.blueprints import user, error, costume
from costumizer.database import setup_pool
from costumizer.auth import get_secret
import flask


def create_app() -> flask.Flask:
    setup_pool()

    app = flask.Flask(__name__)

    app.config["TOKEN_SECRET"] = get_secret()

    app.register_blueprint(error)

    app.register_blueprint(user, url_prefix="/api/user/")
    app.register_blueprint(costume, url_prefix="/api/costume/")

    return app
