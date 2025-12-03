from dotenv import load_dotenv

from mysql.connector import pooling
import os

load_dotenv()

connection_pool = pooling.MySQLConnectionPool(
    pool_size=5,
    host=os.getenv("DB_HOST"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD"),
    database=os.getenv("DB_DATABASE"),
    port=int(os.getenv("DB_PORT")),
    charset="utf8"
)



def get_connection():
    return connection_pool.get_connection()
