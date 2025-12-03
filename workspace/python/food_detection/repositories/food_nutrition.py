from typing import Optional
from models.food_nutrition import Food
from repositories.connections import get_connection


class FoodNutritionLoadError(Exception): pass

class FoodNutritionRepository:
    def __init__(self):
        self._conn = get_connection()
        self.table_name = "FOOD_NUTRITION"
        self._load_fields()

        if self.fields is None:
            raise Exception

    def __del__(self):
        self._conn.close()

    def _load_fields(self) -> None:
        cursor = None
        try:
            cursor = self._conn.cursor()
            cursor.execute(f"DESCRIBE {self.table_name}")
            self.fields = [row[0].lower() for row in cursor.fetchall()]
        except Exception as e:
            self.fields = None
            print(e)
        finally:
            if cursor:
                cursor.close()

    def get_food_nutrition_by_name(self, food_name: str) -> Optional[dict]:
        try:
            cursor = self._conn.cursor()
            sql = f"""
            SELECT * FROM {self.table_name}
            WHERE FOOD_NAME = %s
                    """

            cursor.execute(sql, (food_name,))
            row = cursor.fetchone()

            if not row:
                return None

            return dict(zip(self.fields, row))
        finally:
            if cursor:
                cursor.close()

    def insert_food_nutrition(self, food: Food) -> None:
        try:
            cursor = self._conn.cursor()

            sql = f"""
                INSERT INTO {self.table_name} (
                    food_name,
                    category,
                    calories_kcal,
                    carbs_g,
                    protein_g,
                    fat_g,
                    sugar_g,
                    fiber_g,
                    sodium_mg,
                    calcium_mg
                ) VALUES (
                    %(food_name)s,
                    %(category)s,
                    %(calories_kcal)s,
                    %(carbs_g)s,
                    %(protein_g)s,
                    %(fat_g)s,
                    %(sugar_g)s,
                    %(fiber_g)s,
                    %(sodium_mg)s,
                    %(calcium_mg)s
                )
            """
            cursor.execute(sql, food)
            self._conn.commit()
        finally:
            if cursor:
                cursor.close()
