from fastapi import APIRouter, HTTPException, UploadFile, File
from services.food_detection import detect_food

router = APIRouter(prefix="/food", tags=["food"])


@router.post("/detect")
async def detect_food_route(file: UploadFile = File(...)):
    if file is None:
        raise HTTPException(status_code=400, detail="파일 없음")

    if file.content_type not in ["image/png", "image/jpeg"]:
        raise HTTPException(status_code=400, detail="잘못된 파일 유형")

    content = await file.read()

    if len(content) == 0:
        raise HTTPException(status_code=400, detail="빈 파일")

    nutrition_items = await detect_food(content)

    return {
        "items": nutrition_items
    }
