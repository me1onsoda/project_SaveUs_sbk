from typing import Optional, Dict, Any

from api.api_client import APIClient

import os
import asyncio

API_KEY = os.getenv("QR_API_KEY")


class FoodQrClient:
    FIELD_MAP = {
                    # 'prdctNm': 'food_name',
                   # 'indctGroupNm': 'category',
                   '열량': 'calories_kcal',
                   '단백질': 'protein_g',
                   '지방': 'fat_g',
                   '탄수화물': 'carbs_g',
                   '당류': 'sugar_g',
                   '식이섬유': 'fiber_g',
                   '칼슘': 'calcium_mg',
                   '나트륨': 'sodium_mg'
                }

    def __init__(self) -> None:
        self.client = APIClient(
            base_url=f"https://foodqr.kr/openapi/service/qr1008/F008",
            default_params={
                "accessKey":API_KEY,
                "numOfRows":10,
                "pageNo":1,
                "_type":"json"
            }
        )

    async def _fetch_response(self, query_params: Optional[Dict[str,Any]]):
        return await self.client.fetch(query_params)

    async def get_food_data(self, query_params: Optional[Dict[str,Any]]) :
        food_data = {}
        response = await self._fetch_response(query_params)
        rows = response.get("response").get("body").get("items")
        if rows:
            row = rows.get("item")
            for nutrition in row:
                if (raw_key := nutrition["nirwmtNm"]) in self.FIELD_MAP:
                    field_name = self.FIELD_MAP[raw_key]
                    try:
                        value = float(nutrition["cta"])
                    except (ValueError, TypeError):
                        value = 0
                    food_data[field_name] = value
            food_data["food_name"] = row[0]["prdctNm"]
            food_data["category"] = row[0]["indctGroupNm"]
        return food_data



if __name__ == '__main__':
    imrptNo = "20010549541125"
    fetcher = FoodQrClient()
    res = asyncio.run(fetcher.get_food_data({
        "imrptNo":imrptNo
    }))
    print(res)
