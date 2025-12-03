from typing import Optional

from pydantic import BaseModel

class Food(BaseModel):
    food_id: Optional[int]
    food_name: str
    category: Optional[str]
    calories_kcal: float
    carbs_g: Optional[float] = None
    protein_g: Optional[float] = None
    fat_g: Optional[float] = None
    sugar_g: Optional[float] = None
    fiber_g: Optional[float] = None
    sodium_mg: Optional[float] = None
    calcium_mg: Optional[float] = None
