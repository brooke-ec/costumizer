from costumizer.errors import NoRecordError
from mysql.connector import DatabaseError
from contextlib import contextmanager
import costumizer.config as config
from typing import TypedDict
from flask import abort
import mysql.connector

POOL_NAME = "costumizer"


def setup_pool() -> None:
    if not config.DB_POOL_SIZE.isnumeric():
        raise TypeError("pool_size must be an integer.")
    con = mysql.connector.connect(
        pool_name=POOL_NAME,
        pool_size=int(config.DB_POOL_SIZE),
        host=config.DB_HOST,
        username=config.DB_USER,
        password=config.DB_PASSWORD,
        database=config.DB_DATABASE,
    )
    con.close()


@contextmanager
def connect():
    with mysql.connector.connect(pool_name=POOL_NAME) as conn:
        with conn.cursor() as cur:
            try:
                yield cur
            except DatabaseError as e:
                conn.rollback()
                abort(500, f"Database Error: {e.errno}")
        conn.commit()


class CostumesListType(TypedDict):
    name: str
    hash: str
    slim: bool


def get_costumes_list(uuid: str) -> list[CostumesListType]:
    with connect() as cur:
        cur.execute(
            """SELECT c.name, s.hash, s.slim FROM costume c
            INNER JOIN skin s ON c.skin = s.hash WHERE c.owner = %s
            ORDER BY c.name""",
            (uuid,),
        )
        return [{"name": c[0], "hash": c[1], "slim": bool(c[2])} for c in cur.fetchall()]


class CostumeInfoType(TypedDict):
    display: str
    slim: bool
    name: str
    hash: str


def get_costume_info(uuid: str, name: str) -> CostumeInfoType:
    with connect() as cur:
        cur.execute(
            """SELECT c.name, c.display, s.hash, s.slim FROM costume c
        INNER JOIN skin s ON c.skin = s.hash WHERE c.owner = %s AND c.name = %s""",
            (uuid, name),
        )
        c = cur.fetchone()
        if c is None:
            raise NoRecordError("Could not find costume.")
        return {"name": c[0], "display": c[1], "hash": c[2], "slim": bool(c[3])}


def get_costume_existence(uuid: str, name: str) -> bool:
    with connect() as cur:
        cur.execute(
            """SELECT c.name FROM costume c WHERE c.owner = %s AND c.name = %s""",
            (uuid, name),
        )
        c = cur.fetchone()
        return c is not None


class SkinType(TypedDict):
    slim: bool
    hash: str


def get_costume_skin(uuid: str, name: str) -> SkinType:
    with connect() as cur:
        cur.execute(
            """SELECT s.hash, s.slim FROM costume c
        INNER JOIN skin s ON c.skin = s.hash WHERE c.owner = %s AND c.name = %s""",
            (uuid, name),
        )
        c = cur.fetchone()
        if c is None:
            raise NoRecordError("Could not find costume.")
        return {"hash": c[0], "slim": bool(c[1])}


def update_costume(uuid: str, name: str, new_name: str, skin: str, display: str):
    with connect() as cur:
        cur.execute(
            "UPDATE costume SET name=%s, skin=%s, display=%s WHERE owner=%s AND name=%s",
            (new_name, skin, display, uuid, name),
        )
