from typing import Optional, List

from repositories import FoodNutritionRepository
from api import FoodNutritionClient, ProductInfoClient, FoodQrClient
from utils.barcode_detector import BarcodeDetector
from ml.yolo_inference import detect_objects
from models.food_nutrition import Food
from utils.mapper import label_map


food_nutrition_repository = FoodNutritionRepository()

async def detect_food(imageBytes) -> Optional[List[Food]]:
    product_info_client = ProductInfoClient()
    food_nutrition_client = FoodNutritionClient()
    food_qr_client = FoodQrClient()
    barcode_detector = BarcodeDetector()

    detected_barcode = barcode_detector.detect_codes_from_file(imageBytes)

    if not detected_barcode:
        detected_foods = await detect_objects(imageBytes)
        items = [label_map.get(res) for res in detected_foods]
        nutrition_items = [Food(**food_info) for item in items if (food_info := food_nutrition_repository.get_food_nutrition_by_name(item))]
        return nutrition_items

    barcode = detected_barcode[0]
    print(f"detected barcode: {barcode}")

    prd_no = await product_info_client.get_prd_report_no(barcode)

    print(f"prd_report_no: {prd_no}")
    if prd_no is None:
        return None

    nutrition_item = await food_nutrition_client.get_food_data(
        query_params={
            "ITEM_REPORT_NO": prd_no,
        }
    )

    if not nutrition_item:
        nutrition_item = await food_qr_client.get_food_data(
            query_params={
                "imrptNo": prd_no
            }
        )
    print(f"nutrition_item: {nutrition_item}")
    return [nutrition_item]
