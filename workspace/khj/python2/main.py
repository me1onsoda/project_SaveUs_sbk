from fastapi import FastAPI
from pydantic import BaseModel
import pandas as pd
import joblib
import requests

from food_recommender import (
    filter_processed,
    map_deficit_to_cluster
)

app = FastAPI()

# ------------------------------------------------------------
# 0. Spring 서버 주소
# ------------------------------------------------------------
SPRING_URL = "http://3.37.90.119:8080"


# ------------------------------------------------------------
# 1. 음식 DB + 모델 로드
# ------------------------------------------------------------
try:
    df_food = pd.read_csv("food_clustered.csv")
    model = joblib.load("food_recommend_model.pkl")
    print("모델 로드 완료: food_recommend_model.pkl")
except:
    raise Exception("food_clustered.csv 또는 food_recommend_model.pkl 파일을 찾을 수 없습니다.")


# ------------------------------------------------------------
# 2. Request 형식 정의 (userId만 받음)
# ------------------------------------------------------------
class RecommendRequest(BaseModel):
    user_id: int


# ------------------------------------------------------------
# 3. Spring API 호출 (목표 조회)
# ------------------------------------------------------------
def get_user_goal(user_id: int):
    url = f"{SPRING_URL}/api/goal/{user_id}"
    response = requests.get(url)

    if response.status_code != 200:
        raise Exception("Spring 서버에서 사용자 목표를 가져오지 못했습니다.")

    return response.json()


# ------------------------------------------------------------
# 4. Spring API 호출 (오늘 섭취량 조회)
# ------------------------------------------------------------
def get_today_nutrition(user_id: int):
    url = f"{SPRING_URL}/api/nutrition/today/{user_id}"
    response = requests.get(url)

    if response.status_code != 200:
        raise Exception("Spring 서버에서 오늘 섭취량을 가져오지 못했습니다.")

    return response.json()


# ------------------------------------------------------------
# 5. 목표 대비 부족 분석 함수
# ------------------------------------------------------------
def detect_deficit_by_goal(goal, current):
    deficit_list = []

    def ratio(cur, tgt):
        if tgt == 0:
            return 1
        return cur / tgt

    protein_rate = ratio(current["protein"], goal["goal_protein"])
    carbs_rate   = ratio(current["carbs"], goal["goal_carbs"])
    fat_rate     = ratio(current["fat"], goal["goal_fat"])
    cal_rate     = ratio(current["calories"], goal["goal_calories"])

    # 1) 단백질
    if protein_rate < 0.3:
        deficit_list.append("high_protein")
    elif protein_rate < 0.6:
        deficit_list.append("mid_protein")

    # 2) 식이섬유
    if current["fiber"] < 20:
        deficit_list.append("high_fiber")

    # 3) 탄수화물
    if carbs_rate < 0.3:
        deficit_list.append("high_carbs")
    elif carbs_rate < 0.6:
        deficit_list.append("mid_carbs")

    # 4) 지방
    if fat_rate < 0.3:
        deficit_list.append("high_fat")
    elif fat_rate < 0.6:
        deficit_list.append("mid_fat")

    # 5) 칼로리
    if cal_rate < 0.3:
        deficit_list.append("high_calorie")
    elif cal_rate < 0.6:
        deficit_list.append("mid_calorie")

    return deficit_list


# ------------------------------------------------------------
# 6. 음식 추천 로직
# ------------------------------------------------------------
def recommend_menu(goal, current, df):
    df_filtered = filter_processed(df)

    deficit = detect_deficit_by_goal(goal, current)
    cluster_target = map_deficit_to_cluster(deficit)

    # 부족 없음 → 랜덤 추천
    if cluster_target is None:
        return df_filtered.sample(5)[["food_name", "category"]].to_dict(orient="records")

    # 해당 클러스터 추천
    recommended = df_filtered[df_filtered["cluster"] == cluster_target]

    # 데이터 부족 시 랜덤
    if len(recommended) < 5:
        recommended = df_filtered.sample(5)

    return recommended.sample(5)[["food_name", "category"]].to_dict(orient="records")


# ------------------------------------------------------------
# 7. FastAPI 엔드포인트
# ------------------------------------------------------------
@app.post("/food/recommend")
def recommend(request: RecommendRequest):

    # 1) 목표 조회
    goal = get_user_goal(request.user_id)

    # 2) 오늘 섭취량 조회
    current = get_today_nutrition(request.user_id)

    # 3) 추천 수행
    result = recommend_menu(goal, current, df_food)

    return {
        "user_id": request.user_id,
        "deficit": detect_deficit_by_goal(goal, current),
        "recommended": result
    }
