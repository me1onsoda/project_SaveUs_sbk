from typing import Optional

import oracledb

CONNECT_STR = "sqlid/sqlpw@127.0.0.1:1521/orcl"

def get_food_nutrition_by_name(food_name: str) -> Optional[dict[str, object]]:
    connection = None
    cursor = None
    row_dict = None
    try:
        connection = oracledb.connect(CONNECT_STR)
        cursor = connection.cursor()

        query = f"""
        select * from food_nutrition
        where food_name = :food_name
                       """

        cursor.execute(query, {"food_name": food_name})

        row = cursor.fetchone()

        columns = [col[0].lower() for col in cursor.description]
        row_dict = dict(zip(columns, row))

    except oracledb.DatabaseError as e:
        error_obj = e.args[0]
        print("Oracle-Error-Code:", error_obj.code)
        print("Oracle-Error-Message:", error_obj.message)
    finally:
        if cursor is not None:
            cursor.close()
        if connection is not None:
            connection.close()

        return row_dict


if __name__ == "__main__":
    print(get_food_nutrition_by_name("고구마맛탕"), sep="\n")
    print(get_food_nutrition_by_name("가방"), sep="\n")
    print(get_food_nutrition_by_name(""), sep="\n")
