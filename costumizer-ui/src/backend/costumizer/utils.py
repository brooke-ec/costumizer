from typing import TypedDict, Any, TypeVar, Type, get_type_hints
from flask import request, abort


def get_parameter(key: str) -> str:
    data = request.args.get(key)
    if data is None:
        abort(400, f"The required parameter '{key}' was omitted.")
    return data


T = TypeVar("T")


def get_body(type: Type[T]) -> T:
    if not request.is_json:
        abort(400, "Content-Type is not 'application/json'")
    try:
        data = request.json
        if data is None:
            raise Exception()
    except Exception:
        abort(400, "Body is not valid JSON")

    return check_typing(type, data)


def check_typing(type: Type[T], data: dict) -> T:
    result = {}
    for name, type in get_type_hints(type).items():
        if name not in data:
            abort(400, f"Required property '{name}' was omitted")
        value = data[name]
        result[name] = value
        if type is Any:
            continue
        if type is TypedDict:
            if not isinstance(value, dict):
                abort(400, f"Property '{name}' must be type of object")
            value = check_typing(type, value)
        else:
            if not isinstance(value, type):
                abort(400, f"Property '{name}' does not meet type constraint '{type}'")

    return result  # type: ignore
