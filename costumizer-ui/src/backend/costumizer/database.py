import costumizer.config as config
from typing import TypedDict
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


class GetCostumesType(TypedDict):
    name: str
    hash: str
    slim: bool


def get_costumes(uuid: str) -> list[GetCostumesType]:
    with mysql.connector.connect(pool_name=POOL_NAME) as conn:
        cur = conn.cursor()
        cur.execute(
            """SELECT c.name, s.hash, s.slim FROM costume c
            INNER JOIN skin s ON c.skin = s.hash WHERE owner = %s""",
            (uuid,),
        )
        return [{"name": c[0], "hash": c[1], "slim": bool(c[2])} for c in cur.fetchall()]
