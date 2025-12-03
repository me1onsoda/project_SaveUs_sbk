import pandas as pd
import numpy as np
import xgboost as xgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, roc_auc_score
import joblib


# =============================================================================
# [1] 데이터 로드 및 전처리
# =============================================================================
def load_and_preprocess_data():
    print("\n>>> [1/6] 데이터 로딩 시작...")

    def load_sas(path, year_label):
        try:
            d = pd.read_sas(path)
            d.columns = d.columns.str.upper()
            print(f"- {year_label} 데이터 로드 완료: {len(d)}명")
            return d
        except Exception as e:
            return None

    df_list = [
        load_sas('hn23_all.sas7bdat', '2023년'),
        load_sas('hn22_all.sas7bdat', '2022년'),
        load_sas('hn21_all.sas7bdat', '2021년'),
        load_sas('hn20_all.sas7bdat', '2020년')
    ]
    df_list = [d for d in df_list if d is not None]
    if not df_list: return None
    df = pd.concat(df_list, ignore_index=True)

    print(">>> [2/6] 전처리 및 파생변수 생성")

    # ---------------------------------------------------------
    # 미성년자 제거 (만 19세 이상만 사용)
    # ---------------------------------------------------------
    before_n = len(df)
    df = df[df['AGE'] >= 19]
    print(f"- 성인 필터링: {before_n}명 -> {len(df)}명 (미성년자 제외)")

    # ---------------------------------------------------------
    # 이미 당뇨 진단받은 사람 제거 (선택 사항이나 추천)
    # DE1_DG: 당뇨병 의사진단 여부 (1: 있음, 0: 없음, 8: 비해당 등)
    # ---------------------------------------------------------
    # 진단받은 사람(1)을 제외하여, '식단 때문에 혈당이 높은 미진단자'만 학습
    if 'DE1_DG' in df.columns:
        df = df[df['DE1_DG'] != 1]
        print(f"- 기진단자 제외: 완료 (순수 식단 영향력 분석을 위해)")

    # 필수 컬럼 및 결측치 제거
    required_cols = ['AGE', 'SEX', 'HE_HT', 'HE_WT', 'N_EN', 'N_CHO', 'N_FAT', 'N_PROT', 'N_NA', 'N_SUGAR', 'HE_GLU']
    available_cols = [c for c in required_cols if c in df.columns]
    df = df[available_cols].dropna()

    # BMI 계산
    df['BMI'] = df['HE_WT'] / ((df['HE_HT'] / 100) ** 2)

    # 비현실적 식단 제거
    df = df[(df['N_EN'] > 500) & (df['N_CHO'] > 0)].copy()

    # 활동대사량
    df['REC_CALORIE'] = df.apply(lambda r: (r['HE_HT'] - 100) * 0.9 * (30 if r['SEX'] == 1 else 25), axis=1)

    # 칼로리 충족률
    df['PCT_CALORIE'] = df['N_EN'] / df['REC_CALORIE']
    # 단백질 충족률 (체중 1kg당 1g 권장 기준)
    df['PCT_SODIUM'] = df['N_NA'] / 2000.0
    # 탄수화물 질 (당류 비율)
    df['RATIO_FAT'] = (df['N_FAT'] * 9) / df['N_EN']
    # 나트륨 위험도 (2000mg 기준)
    df['RATIO_CHO'] = (df['N_CHO'] * 4) / df['N_EN']
    #  지방 비율 (총 칼로리 중 지방이 차지하는 비율)
    df['RATIO_SUGAR_TO_CHO'] = df['N_SUGAR'] / df['N_CHO']
    # 탄수화물 비율
    df['PCT_PROTEIN'] = df['N_PROT'] / df['HE_WT']

    # 로그 변환
    df['LOG_PCT_CALORIE'] = np.log1p(df['PCT_CALORIE'])
    df['LOG_PCT_SODIUM'] = np.log1p(df['PCT_SODIUM'])
    df['LOG_RATIO_FAT'] = np.log1p(df['RATIO_FAT'])

    # Target 설정 (공복혈당 100 이상)(전당뇨/위험군)
    target_threshold = 100
    df['Target'] = (df['HE_GLU'] >= target_threshold).astype(int)

    return df


# =============================================================================
# [2] 모델 학습 (단조 제약 조건 적용 - 역설 해결)
# =============================================================================
def train_model(df):
    print("\n>>> [3/6] 모델 학습 시작 (XGBoost with Constraints)")

    features = [
        'AGE', 'BMI', 'SEX',
        'LOG_PCT_CALORIE', 'PCT_PROTEIN',
        'LOG_PCT_SODIUM', 'RATIO_SUGAR_TO_CHO',
        'LOG_RATIO_FAT', 'RATIO_CHO'
    ]

    # 제약 조건 (1: 위험 증가, -1: 위험 감소, 0: 무관)
    # AGE(+), BMI(+), SEX(0), CAL(+), PROT(-), SOD(+), SUGAR(+), FAT(+), CHO(+)
    mono_constraints = (1, 1, 0, 1, -1, 1, 1, 1, 1)

    X = df[features]
    y = df['Target']

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

    model = xgb.XGBClassifier(
        n_estimators=300,
        max_depth=6,
        learning_rate=0.03,
        subsample=0.8,
        colsample_bytree=0.8,
        random_state=42,
        eval_metric='logloss',
        monotone_constraints=mono_constraints
    )

    model.fit(X_train, y_train)

    joblib.dump(model, 'diabetes_risk_model_v5_xgb.pkl')
    print("모델 저장 완료: diabetes_risk_model_v5_xgb.pkl")

    # ---------------------------------------------------------
    # 모델 성능 평가 (Evaluation)
    # ---------------------------------------------------------
    print("\n" + "=" * 50)
    print("[모델 성능 평가 리포트]")
    print("=" * 50)

    y_pred = model.predict(X_test)  # 예측 결과 (0 or 1)
    y_proba = model.predict_proba(X_test)[:, 1]  # 위험 확률 (0.0 ~ 1.0)

    # 정밀도, 재현율 등 상세 지표 출력
    print("\n1. Classification Report:")
    print(classification_report(y_test, y_pred))

    # AUC 점수 계산 (0.5=랜덤, 1.0=완벽)
    auc = roc_auc_score(y_test, y_proba)
    print(f"AUC Score: {auc:.4f}")

    # AUC 해석
    if auc >= 0.7:
        print("   -> 훌륭합니다! (의료 데이터 기준 실뢰할 수 있는 수준)")
    elif auc >= 0.6:
        print("   -> 나쁘지 않지만 개선이 필요합니다.")
    else:
        print("   -> 학습이 제대로 되지 않았습니다.")

    # 변수 중요도 출력
    print("\n2. 변수 중요도 (Top Features):")
    imp = pd.Series(model.feature_importances_, index=features).sort_values(ascending=False)
    print(imp)

    return model, features


# =============================================================================
# [3] 스코어링 함수 (상대평가 적용)
# =============================================================================
def calculate_fair_diet_score(model, user_input_dict, feature_names):
    input_df = pd.DataFrame([user_input_dict])

    # 1. 실제 사용자의 위험도 (참고용)
    real_risk = model.predict_proba(input_df[feature_names])[:, 1][0]

    # 2. 아바타 생성 (52세, BMI 26 남성 - 경계인) 너무 한쪽으로 치우쳐지면 왜곡된 결과가 나올 가능성
    avatar_df = input_df.copy()
    avatar_df['AGE'] = 52
    avatar_df['BMI'] = 26.0
    avatar_df['SEX'] = 1

    avatar_risk = model.predict_proba(avatar_df[feature_names])[:, 1][0]

    # 3. 상대평가 점수 매핑 (데이터 분포 기반)
    # Min(0.2) ~ Top10%(0.53) ~ Mean(0.60) ~ Max(0.9)
    if avatar_risk <= 0.20:
        score = 100
    elif avatar_risk <= 0.53:
        ratio = (0.53 - avatar_risk) / (0.53 - 0.20)
        score = 80 + (ratio * 20)
    elif avatar_risk <= 0.68:
        ratio = (0.68 - avatar_risk) / (0.68 - 0.53)
        score = 40 + (ratio * 40)
    else:
        ratio = (0.91 - avatar_risk) / (0.91 - 0.68)
        score = ratio * 40

    final_score = int(np.clip(score, 0, 100))

    return real_risk, final_score


# =============================================================================
# 결과 해석 출력
# =============================================================================
def print_user_result(real_prob, diet_score):
    """
    사용자에게 결과를 보여주는 로직
    """
    similarity = 100 - diet_score

    print(f"\n==================================================")
    print(f"[AI 식단 분석 결과]")
    print(f"==================================================")

    if diet_score >= 80:
        print(f"**식단 점수: {diet_score}점** (매우 훌륭함)")
        print(f"   \"완벽합니다! 이대로만 드시면 대사질환 걱정은 없습니다.\"")
        print(f"   -> [Tip] 지금의 식습관을 꾸준히 유지하세요.")

    elif diet_score >= 50:
        print(f"**식단 점수: {diet_score}점** (주의 필요)")
        print(f"   \"조금 아쉬워요. 전당뇨 위험 식단 패턴과 {similarity}% 유사합니다.\"")
        print(f"   -> [Tip] 탄수화물이나 나트륨을 조금만 줄여보세요. 점수가 올라갑니다!")

    else:
        print(f"**식단 점수: {diet_score}점** (위험!)")
        print(f"   \"경고! 이 식단은 혈당이 높은 환자들의 식사와 {similarity}%나 일치합니다.\"")
        print(f"   -> [Tip] 다음 끼니는 반드시 채소 비중을 높여야 합니다.")

    print(f"==================================================\n")
    # print(f"(참고: 현재 신체 스펙 포함 실제 위험도는 {int(real_prob*100)}% 입니다.)")


# =============================================================================
# 실행
# =============================================================================
if __name__ == "__main__":
    df = load_and_preprocess_data()

    if df is not None:
        model, features = train_model(df)

        print("\n" + "=" * 50)
        print("[최종 검증 시뮬레이션]")
        print("=" * 50)

        # User A: 샐러드 (Safe)
        user_salad = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(0.6), 'PCT_PROTEIN': 1.5,
            'LOG_PCT_SODIUM': np.log1p(0.3), 'RATIO_SUGAR_TO_CHO': 0.05,
            'LOG_RATIO_FAT': np.log1p(0.20), 'RATIO_CHO': 0.40
        }

        # User B: 마라탕 (Danger)
        user_maratang = {
            'AGE': 20, 'BMI': 18, 'SEX': 2,
            'LOG_PCT_CALORIE': np.log1p(1.5), 'PCT_PROTEIN': 0.8,
            'LOG_PCT_SODIUM': np.log1p(2.0), 'RATIO_SUGAR_TO_CHO': 0.50,
            'LOG_RATIO_FAT': np.log1p(0.45), 'RATIO_CHO': 0.60
        }

        # 결과 출력
        print("\n--- [User A: 닭가슴살 샐러드] ---")
        real_a, score_a = calculate_fair_diet_score(model, user_salad, features)
        print_user_result(real_a, score_a)

        print("\n--- [User B: 마라탕 & 탕후루] ---")
        real_b, score_b = calculate_fair_diet_score(model, user_maratang, features)
        print_user_result(real_b, score_b)

        # ---------------------------------------------------------
        # User C: K-직장인 (김치찌개 + 공기밥 + 반찬)
        # 특징: 탄수화물 높음, 나트륨 매우 높음, 단백질 보통
        # ---------------------------------------------------------
        user_k_worker = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(1.1),  # 점심 든든하게 (약간 과식)
            'PCT_PROTEIN': 1.0,  # 고기 조금 들어감 (평범)
            'LOG_PCT_SODIUM': np.log1p(1.8),  # 찌개 국물 드링킹 (나트륨 3600mg 수준)
            'RATIO_SUGAR_TO_CHO': 0.10,  # 당류는 낮음
            'LOG_RATIO_FAT': np.log1p(0.25),  # 지방 보통
            'RATIO_CHO': 0.65  # 탄수화물 비중 높음 (밥 위주)
        }

        # ---------------------------------------------------------
        # User D: 극단적 저탄고지 (삼겹살 + 버터 + 채소 조금)
        # 특징: 지방 매우 높음, 탄수화물 극소량, 칼로리 높음
        # ---------------------------------------------------------
        user_keto = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(1.2),  # 고칼로리
            'PCT_PROTEIN': 1.2,  # 단백질 양호
            'LOG_PCT_SODIUM': np.log1p(0.5),  # 쌈장 조금 (나트륨 적음)
            'RATIO_SUGAR_TO_CHO': 0.05,  # 당류 거의 없음
            'LOG_RATIO_FAT': np.log1p(0.65),  # 지방 비중 65% (매우 높음 -> 모델이 싫어할 수 있음)
            'RATIO_CHO': 0.10  # 탄수화물 10% (극단적 제한)
        }

        # ---------------------------------------------------------
        # User E: 편의점 인스턴트 (컵라면 + 삼각김밥)
        # 특징: 정제 탄수화물 폭발, 단백질 부족, 나트륨 높음
        # ---------------------------------------------------------
        user_instant = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(0.9),  # 칼로리 자체는 보통
            'PCT_PROTEIN': 0.5,  # 단백질 심각하게 부족
            'LOG_PCT_SODIUM': np.log1p(1.5),  # 라면 국물 (나트륨 높음)
            'RATIO_SUGAR_TO_CHO': 0.15,  # 알게 모르게 당류 있음
            'LOG_RATIO_FAT': np.log1p(0.35),  # 팜유/튀김 지방 높음
            'RATIO_CHO': 0.70  # 탄수화물 70% (매우 높음)
        }

        # ---------------------------------------------------------
        # User F: 헬창 (닭가슴살 + 고구마 + 현미밥 대량)
        # 특징: 클린 식단이지만 '과식(벌크업)', 단백질 초과다
        # ---------------------------------------------------------
        user_gym_lover = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(1.5),  # 벌크업 중이라 많이 먹음 (150% 섭취)
            'PCT_PROTEIN': 2.5,  # 단백질 체중당 2.5g (초고단백)
            'LOG_PCT_SODIUM': np.log1p(0.3),  # 저염
            'RATIO_SUGAR_TO_CHO': 0.05,  # 당류 없음
            'LOG_RATIO_FAT': np.log1p(0.20),  # 지방 절제
            'RATIO_CHO': 0.55  # 탄수화물 적정 비율 유지
        }

        # ---------------------------------------------------------
        # User G: 디저트 충 (밥 안 먹고 케이크 + 프라푸치노)
        # 특징: 칼로리 낮음(소식), but 영양 불균형(당류/지방 몰빵)
        # ---------------------------------------------------------
        user_dessert = {
            'AGE': 99, 'BMI': 99, 'SEX': 1,
            'LOG_PCT_CALORIE': np.log1p(0.7),  # 입맛 없어서 조금만 먹음
            'PCT_PROTEIN': 0.2,  # 단백질 거의 없음 (0점 수준)
            'LOG_PCT_SODIUM': np.log1p(0.2),  # 짠건 안 먹음
            'RATIO_SUGAR_TO_CHO': 0.70,  # 탄수화물의 70%가 설탕 (최악)
            'LOG_RATIO_FAT': np.log1p(0.40),  # 크림/버터 지방 높음
            'RATIO_CHO': 0.50  # 비율상 탄수화물은 보통
        }

        # user_test = {
        #     'AGE': 99, 'BMI': 99, 'SEX': 1,
        #     'LOG_PCT_CALORIE': np.log1p(),  # 입맛 없어서 조금만 먹음
        #     'PCT_PROTEIN': 0.2,  # 단백질 거의 없음 (0점 수준)
        #     'LOG_PCT_SODIUM': np.log1p(0.2),  # 짠건 안 먹음
        #     'RATIO_SUGAR_TO_CHO': 0.70,  # 탄수화물의 70%가 설탕 (최악)
        #     'LOG_RATIO_FAT': np.log1p(0.40),  # 크림/버터 지방 높음
        #     'RATIO_CHO': 0.50  # 비율상 탄수화물은 보통
        # }

        # ==========================================
        # 결과 출력 실행
        # ==========================================

        print("\n--- [User C: 김치찌개 백반] ---")
        real_c, score_c = calculate_fair_diet_score(model, user_k_worker, features)
        print_user_result(real_c, score_c)

        print("\n--- [User D: 저탄고지 (삼겹살)] ---")
        real_d, score_d = calculate_fair_diet_score(model, user_keto, features)
        print_user_result(real_d, score_d)

        print("\n--- [User E: 편의점 라면세트] ---")
        real_e, score_e = calculate_fair_diet_score(model, user_instant, features)
        print_user_result(real_e, score_e)

        print("\n--- [User F: 벌크업 식단 (과식)] ---")
        real_f, score_f = calculate_fair_diet_score(model, user_gym_lover, features)
        print_user_result(real_f, score_f)

        print("\n--- [User G: 케이크 & 커피] ---")
        real_g, score_g = calculate_fair_diet_score(model, user_dessert, features)
        print_user_result(real_g, score_g)