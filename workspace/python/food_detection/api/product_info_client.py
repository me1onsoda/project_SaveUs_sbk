from typing import Optional
from dotenv import load_dotenv

from api.api_client import APIClient

import os
import asyncio

load_dotenv()


API_KEY = os.getenv("BARCODE_API_KEY")

class ProductInfoClient:
    def __init__(self) -> None:
        self.client = APIClient(
            base_url=f"http://openapi.foodsafetykorea.go.kr/api/{API_KEY}/C005/json/1/10/BAR_CD={{}}",
        )

    async def _fetch_response(self):
        response = await self.client.fetch()
        return response

    async def get_prd_report_no(self, barcode_number: str) -> Optional[tuple] :
        self.client.set_path([barcode_number])
        response = await self._fetch_response()

        rows = response.get("C005", {}).get("row", [])
        if rows:
            for row in sorted(rows, key=lambda d: d.get("PRMS_DT", ""), reverse=True):
                if product_number := row.get("PRDLST_REPORT_NO"):
                    return product_number
        return None


if __name__ == '__main__':
    barcode = "8801043015653"
    fetcher = ProductInfoClient()
    res = asyncio.run(fetcher.get_prd_report_no(barcode))
    print(res)
