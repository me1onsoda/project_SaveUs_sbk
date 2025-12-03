from typing import Optional, Dict, Any

from api.api_client import APIClient
from models.food_nutrition import Food

import os


class FoodNutritionClient:
    FIELD_MAP = {'FOOD_NM_KR': 'food_name',
                   'FOOD_CAT1_NM': 'category',
                   'AMT_NUM1': 'calories_kcal',
                   'AMT_NUM3': 'protein_g',
                   'AMT_NUM4': 'fat_g',
                   'AMT_NUM6': 'carbs_g',
                   'AMT_NUM7': 'sugar_g',
                   'AMT_NUM8': 'fiber_g',
                   'AMT_NUM9': 'calcium_mg',
                   'AMT_NUM13': 'sodium_mg'}
    FIELD_TYPE_MAP = {
        k: v.annotation
        for k, v in Food.model_fields.items()
    }

    def __init__(self) -> None:
        self.client = APIClient(
            base_url="https://apis.data.go.kr/1471000/FoodNtrCpntDbInfo02/getFoodNtrCpntDbInq02",
            default_params={
                "serviceKey": os.getenv("NUTRITION_API_KEY"),
                "type": "json",
                "numOfRows": 100,
            },
        )

    async def _fetch_response(self, query_params: Optional[Dict[str, Any]]) -> Dict[str, Any]:
        return await self.client.fetch(query_params)


    async def _fetch_latest_row(self, query_params: Optional[Dict[str, Any]] = None) -> Optional[Dict[str, Any]]:
        response = await self._fetch_response(query_params)

        body = response.get("body", {})
        items = body.get("items", [])
        total_count = body.get("totalCount", 0)

        if total_count < 1:
            return None

        items. sort(key=lambda item: item["UPDATE_DATE"], reverse=True)
        return items[0]

    async def get_food_data(self, query_params: Optional[Dict[str, Any]] = None):
        row = await self._fetch_latest_row(query_params)

        if row is None:
            return None

        food_data: Dict[str, Any] = {}

        for raw_key, raw_value in row.items():
            if raw_key in self.FIELD_MAP:
                field_name = self.FIELD_MAP[raw_key]
                target_type = self.FIELD_TYPE_MAP[field_name]

                if target_type in (int, Optional[int]):
                    try:
                        value = int(raw_value)
                    except (ValueError, TypeError):
                        value = 0
                elif target_type in (float, Optional[float]):
                    try:
                        value = float(raw_value)
                    except (ValueError, TypeError):
                        value = 0.
                else:
                    value = raw_value
                food_data[field_name] = value

        return food_data

