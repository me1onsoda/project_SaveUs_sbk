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

"""
테이블명		FOOD_NUTRITION		작성일	2025년 11월 14일		작성자	임소빈	
주석		식품 및 영양 성분 테이블							
번호	컬럼명	설명		데이터 유형		NULL 여부	기본값		제약조건
1	FOOD_ID	식품 고유 번호(PK)		NUMBER		NOT NULL	자동 증가		PK, GENERATED AS IDENTITY
2	FOOD_NAME	식품명		VARCHAR2(100)		NOT NULL			UNIQUE
3	CATEGORY	식품 분류		VARCHAR2(100)		NOT NULL			
4	SERVING_SIZE	1회 제공량		NUMBER		NULL			
5	SERVING_UNIT	1회 제공 단위		VARCHAR2(10)		NULL			
6	CALORIES_KCAL	칼로리(kcal)		NUMBER		NOT NULL	0		
7	CARBS_G	탄수화물(g)		NUMBER		NULL			
8	PROTEIN_G	단백질(g)		NUMBER		NULL			
9	FAT_G	지방(g)		NUMBER		NULL			
10	SUGAR_G	당류(g)		NUMBER		NULL			
11	DIETARY_FIBER_G	식이섬유(g)		NUMBER		NULL			
12	SATURATED_FAT_G	포화지방(g)		NUMBER		NULL			
13	TRANS_FAT_G	트랜스지방(g)		NUMBER		NULL			
14	SODIUM_MG	나트륨(mg)		NUMBER		NULL			
15	CALCIUM_MG	칼슘(mg)		NUMBER		NULL			
16	IRON_MG	철(mg)		NUMBER		NULL			
17	ZINC_MG	아연(mg)		NUMBER		NULL			
18	MAGNESIUM_MG	마그네슘(mg)		NUMBER		NULL			
19	POTASSIUM_MG	칼륨(mg)		NUMBER		NULL			
20	VITAMIN_C_MG	비타민C(mg)		NUMBER		NULL			
"""



class FoodItem(BaseModel):
    name: str
    category: str
    kcal: float
    carbs: float
    proteins: float
    fats: float
    fiber: float
    sodium: float
    sugar: float
    iron: float
    calcium: float
    magnesium: float
    potassium: float
    vitamin_c: float
    zinc: float

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

    return {
         "items": [
        {
            "name": "김밥 1줄",
            "kcal": 450,
            "carbs": 55,
            "protein": 7,
            "fat": 8
        },
        {
            "name": "떡볶이",
            "kcal": 459,
            "carbs": 102,
            "protein": 9,
            "fat": 2
        }
        ]
    }
