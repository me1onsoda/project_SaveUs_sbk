from typing import Optional

from fastapi import FastAPI, File, UploadFile, HTTPException
from pydantic import BaseModel
from starlette.middleware.cors import CORSMiddleware

test = FastAPI()

test.add_middleware(
    CORSMiddleware,
    allow_origins=["http://127.0.0.1"],
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE"],
    allow_headers=["*"],
)


class FoodItem(BaseModel):
    food_id: int
    food_name: str
    category: str
    calories_kcal: int
    carbs_g: Optional[float] = None
    protein_g: Optional[float] = None
    fat_g: Optional[float] = None
    sugar_g: Optional[float] = None
    fiber_g: Optional[float] = None
    sodium_mg: Optional[float] = None
    calcium_mg: Optional[float] = None


@test.post("/api_test")
async def api_test(
        file: UploadFile = File(...),
):
    if file is None:
        raise HTTPException(status_code=400, detail="파일 없음")

    if file.content_type not in ["image/png", "image/jpeg"]:
        raise HTTPException(status_code=400, detail="잘못된 파일 유형")

    content = await file.read()

    if len(content) == 0:
        raise HTTPException(status_code=400, detail="빈 파일")

    # 파일 확인용 코드
    with open("test.png", "wb") as f:
        f.write(content)

    items = make_dummy_data()

    return {
        "items": items
    }


def make_dummy_data():
    items = []
    items.extend([FoodItem(
        food_id=1,
        food_name="김밥",
        category="밥류",
        carbs_g=55,
        calories_kcal=450,
        protein_g=7,
        fat_g=8,
        sugar_g=6,
        fiber_g=13,
        sodium_mg=10,
        calcium_mg=3
    ),
    FoodItem(
        food_id=2,
        food_name="떡볶이",
        category="볶음류",
        carbs_g=102,
        calories_kcal=450,
        protein_g=9,
        fat_g=2,
        sugar_g=17,
        fiber_g=7,
        sodium_mg=10,
    )
    ])
    return items
