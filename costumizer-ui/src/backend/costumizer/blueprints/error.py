from werkzeug.exceptions import HTTPException
from flask import Blueprint, Response
import json

error = Blueprint("errors", __name__)


@error.app_errorhandler(400)
@error.app_errorhandler(404)
@error.app_errorhandler(500)
@error.app_errorhandler(403)
@error.app_errorhandler(401)
def unauthorized(error: HTTPException):
    return Response(json.dumps({"error": error.description}), error.code)
