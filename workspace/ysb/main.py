from typing import Optional

from fastapi import FastAPI, File, UploadFile, HTTPException
from pydantic import BaseModel
from starlette.middleware.cors import CORSMiddleware

from food_nutrition_repository import get_food_nutrition_by_name
from yolo_inference import detect_objects

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
    calories_kcal: float
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

    # 사진 전송 및 모델 작동 확인 코드
    print(detect_objects(content))

    items = make_dummy_data()

    return {
        "items": items
    }

def make_dummy_data(items=None):
    items = items or ["김밥", "떡볶이", "yolo"]
    return [FoodItem(**food_info) for item in items if (food_info:=get_food_nutrition_by_name(item))]


if __name__ == "__main__":
    print(make_dummy_data())
