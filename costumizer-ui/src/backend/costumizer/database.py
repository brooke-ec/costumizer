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
    resource: str
    slim: bool


def get_costumes_list(owner: str) -> list[CostumesListType]:
    with connect() as cur:
        cur.execute(
            """SELECT c.name, s.resource, s.slim FROM costume c
            INNER JOIN skin s ON c.skin = s.id WHERE c.owner = %s
            ORDER BY c.name""",
            (owner,),
        )
        return [
            {"name": c[0], "resource": c[1], "slim": bool(c[2])} for c in cur.fetchall()
        ]


class CostumeInfoType(TypedDict):
    display: str
    slim: bool
    name: str
    resource: str


def get_costume_info(owner: str, name: str) -> CostumeInfoType:
    with connect() as cur:
        cur.execute(
            """SELECT c.name, c.display, s.resource, s.slim FROM costume c
        INNER JOIN skin s ON c.skin = s.id WHERE c.owner = %s AND c.name = %s""",
            (owner, name),
        )
        d = cur.fetchone()
        if d is None:
            raise NoRecordError()
        return {"name": d[0], "display": d[1], "resource": d[2], "slim": bool(d[3])}


def get_costume_existence(owner: str, name: str) -> bool:
    with connect() as cur:
        cur.execute(
            """SELECT c.name FROM costume c WHERE c.owner = %s AND c.name = %s""",
            (owner, name),
        )
        d = cur.fetchone()
        return d is not None


class SkinType(TypedDict):
    resource: str
    slim: bool
    hash: str
    id: int


def get_costume_skin(owner: str, name: str) -> SkinType:
    with connect() as cur:
        cur.execute(
            """SELECT s.id, s.hash, s.slim, s.resource FROM costume c
            INNER JOIN skin s ON c.skin = s.id WHERE c.owner = %s AND c.name = %s""",
            (owner, name),
        )
        d = cur.fetchone()
        if d is None:
            raise NoRecordError()
        return {"id": d[0], "hash": d[1], "slim": bool(d[2]), "resource": d[3]}


def update_costume(owner: str, name: str, new_name: str, skin: int, display: str):
    with connect() as cur:
        cur.execute(
            "UPDATE costume SET name=%s, skin=%s, display=%s WHERE owner=%s AND name=%s",
            (new_name, skin, display, owner, name),
        )


def get_skin_id(hash: str, slim: bool) -> int:
    with connect() as cur:
        cur.execute("SELECT id FROM skin WHERE hash=%s AND slim=%s", (hash, slim))
        d = cur.fetchone()
        if d is None:
            raise NoRecordError()
        return d[0]


def insert_skin(
    hash: str,
    slim: bool,
    resource: str,
    properties: str,
    signature: str,
) -> int:
    with connect() as cur:
        cur.execute(
            """INSERT IGNORE INTO skin(hash, slim, resource, properties, signature)
            VALUES (%s, %s, %s, FROM_BASE64(%s), FROM_BASE64(%s))""",
            (hash, slim, resource, properties, signature),
        )
        if type(cur.lastrowid) is not int:
            raise NoRecordError()
        if cur.lastrowid != 0:
            id = cur.lastrowid
        else:
            id = get_skin_id(hash, slim)
        return id


def delete_costume(name: str, owner: str) -> int:
    with connect() as cur:
        cur.execute("DELETE FROM costume WHERE name=%s AND owner=%s", (name, owner))
        return cur.rowcount
