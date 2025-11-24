from fastapi import FastAPI
from db import get_connection
import numpy as np
import joblib


app = FastAPI()


# 1) 유저 기본정보 조회
def get_user_base(user_id):
    conn = get_connection()
    cur = conn.cursor()

    sql = """
        SELECT 
            USER_ID,
            AGE,
            GENDER,
            HEIGHT,
            CURRENT_WEIGHT
        FROM USERS
        WHERE USER_ID = %s
    """

    cur.execute(sql, (user_id,))
    result = cur.fetchone()

    cur.close()
    conn.close()
    return result



# 2) 유저 오늘 영양 섭취 합계 조회
def get_today_nutrition(user_id):
    conn = get_connection()
    cur = conn.cursor()

    sql = """
        SELECT
            IFNULL(SUM(CALORIES_KCAL), 0) AS total_calories,
            IFNULL(SUM(CARBS_G), 0) AS total_carbs,
            IFNULL(SUM(PROTEIN_G), 0) AS total_protein,
            IFNULL(SUM(FATS_G), 0) AS total_fat,
            IFNULL(SUM(SUGAR_G), 0) AS total_sugar,
            IFNULL(SUM(FIBER_G), 0) AS total_fiber,
            IFNULL(SUM(CALCIUM_MG), 0) AS total_calcium,
            IFNULL(SUM(SODIUM_MG), 0) AS total_sodium
        FROM MEAL_ENTRY
        WHERE USER_ID = %s
        AND DATE(EAT_TIME) = CURDATE()
    """

    cur.execute(sql, (user_id,))
    result = cur.fetchone()

    cur.close()
    conn.close()
    return result

# 3) 테스트용 API
@app.get("/user-data/{user_id}")
def user_data(user_id: int):
    base = get_user_base(user_id)
    nutrition = get_today_nutrition(user_id)

    return {
        "user": base,
        "today_nutrition": nutrition
    }

# 모델 및 스케일러 로드
model = joblib.load("obesity_model.pkl")
scaler = joblib.load("scaler.pkl")

# 유저 기본정보 + 영양합계 벡터 변환
def make_input_vector(base, nutrition):
    sex = 1 if base["GENDER"] == "M" else 0
    age = base["AGE"]
    height = base["HEIGHT"]
    weight = base["CURRENT_WEIGHT"]

    total_calories = nutrition["total_calories"]
    total_carbs = nutrition["total_carbs"]
    total_protein = nutrition["total_protein"]
    total_fat = nutrition["total_fat"]
    total_sugar = nutrition["total_sugar"]
    total_fiber = nutrition["total_fiber"]
    total_calcium = nutrition["total_calcium"]
    total_sodium = nutrition["total_sodium"]

    return np.array([[
        sex, age, height, weight,
        total_calories, total_carbs, total_protein, total_fat,
        total_sugar, total_fiber, total_calcium, total_sodium
    ]])

@app.get("/predict-obesity/{user_id}")
def predict_obesity(user_id: int):
    base = get_user_base(user_id)
    nutrition = get_today_nutrition(user_id)

    if base is None:
        return {"error": "유저ID를 찾을 수 없습니다."}

    if nutrition is None:
        return {"error : 영양 데이터가 없습니다."}

    # 입력벡터 생성
    x = make_input_vector(base, nutrition)

    # 스케일링 적용
    x_scaled = scaler.transform(x)

    # 예측 실행
    pred = model.predict(x_scaled)[0]
    prob = model.predict_proba(x_scaled)[0][1] #비만일 확률

    # 브라우저로 JSON 반환
    return {
        "user_id": user_id,
        "obesity": int(pred),
        "probability": float(prob) * 100
    }