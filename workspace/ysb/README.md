# 이미지 기반 식단 분석 API  
  
업로드된 음식 사진 파일을 FastAPI 서버로 전송하면,    
YOLO 모델로 객체 탐지를 수행하고, Oracle DB에 저장된 영양 정보를 조회해 반환하는 API입니다.    

---  
  
## 목차  
  
1. [구성 개요](#1-구성-개요)    
2. [서버 실행 준비](#2-서버-실행-준비)    
   2-1. [의존성 설치](#2-1-의존성-설치)    
   2-2. [서버 실행](#2-2-서버-실행)    
3. [FastAPI 엔드포인트](#3-fastapi-엔드포인트)    
   3-1. [요청](#3-1-요청)    
   3-2. [처리 흐름](#3-2-처리-흐름)    
   3-3. [응답](#3-3-응답)    
4. [Java(Spring) 연동 개요](#4-javaspring-연동-개요)    
   4-1. [호출 방식](#4-1-호출-방식)    
   4-2. [컨트롤러 예시](#4-2-컨트롤러-예시)    
   4-3. [Service 예시](#4-3-service-예시)    
   4-4. [DTO 예시](#4-4-dto-예시)    
5. [전체 동작 흐름 요약](#5-전체-동작-흐름-요약)    
6. [현재 상태](#6-현재-상태)    
7. [동작 예](#7-동작-예)  
  
---  
  
## 1. 구성 개요
  
- 백엔드 서버: Python FastAPI (`main.py`)  
- 엔드포인트: `POST /api_test`  
- 요청 형식: `multipart/form-data` 이미지 파일 1개  
- 응답 형식: `{"items": [음식 정보 리스트]}` 형태의 JSON  
-  데이터 접근 모듈:    
    - `yolo_inference.py`    
    - `food_nutrition_repository.py`  
- 이미지 인식 모델: Ultralytics YOLOv8m (`saveUs_food_detection.pt`)  
- 영양정보 저장소: Oracle DB `food_nutrition` 테이블  
- 클라이언트 예시: Java(Spring)에서 `RestTemplate`로 호출  
  
  
---  
  
## 2. 서버 실행 준비
  
### 2-1. 의존성 설치
  
`requirements.txt` 기준으로 패키지 설치를 진행합니다.  
  
```bash  
# (선택) 가상환경 생성  
python -m venv venv  
  
# 가상환경 활성화 (OS에 따라 택 1)# Windows  
venv\Scripts\activate  
  
# macOS / Linux  
source venv/bin/activate  
  
# 의존성 설치  
pip install -r requirements.txt  
```  
  
### 2-2. 서버 실행
  
가상환경 활성화 후 `main.py`가 있는 디렉토리에서 FastAPI 앱을 실행합니다.  
```bash  
cd path/to/main.py_디렉토리  
uvicorn main:test --reload
```
  
- 앱 인스턴스 이름: `test`  
- 기본 실행 주소: `http://127.0.0.1:8000`  
- CORS 허용 도메인: `http://127.0.0.1`  
  
로그 예시:  
```  
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)  
INFO:     Started reloader process [19924] using WatchFiles  
INFO:     Started server process [13720]  
INFO:     Waiting for application startup.  
INFO:     Application startup complete.  
```  
위와 같은 형태의 메시지가 출력되면 정상 실행된 상태입니다.  
  
  
---  
  
## 3. FastAPI 엔드포인트
  
### 3-1. 요청
- Method: `POST`  
- URL: `http://localhost:8000/api_test`  
- Content-Type: `multipart/form-data`  
- Form 필드  
    - `file`: 이미지 파일  
      - 허용 확장자: `image/png`, `image/jpeg`  
  
유효성 검사:  
1. `file` 존재 여부 (`None` 체크)  
2. `file.content_type` 검사 (`image/png`, `image/jpeg`)  
3. 바이트 길이 검사 검증 실패 시 HTTP 400 에러 반환  
  
### 3-2. 처리 흐름
`main.py`의 `/api_test`의 처리 순서는 아래와 같습니다.  
1. 파일 유효성 검사  
2. 파일 바이너리(`bytes`)를 `yolo_inference.detect_objects`에 전달  
3. `YOLOv8m`을 파인튜닝한 모델로 객체 감지 수행  
4. 감지 결과(`Counter`)를 콘솔에 출력 (현재는 디버깅용 로그만 사용)  
5. `make_dummy_data()` 호출  
   - 음식명 리스트를 사용해 Oracle DB 조회  
   - 각 이름에 대해 `get_food_nutrition_by_name(food_name)` 호출  
   - DB에 존재하는 음식만 필터링  
   - 조회된 행을 `FoodItem` 스키마로 변환  
6. `{"items": [...]}` 형태로 응답 반환  
  
`FoodItem` 필드는 아래와 같습니다.  
- `food_id: int`  
- `food_name: str`  
- `category: str`  
- `calories_kcal: float`  
- `carbs_g: float | null`  
- `protein_g: float | null`  
- `fat_g: float | null`  
- `sugar_g: float | null`  
- `fiber_g: float | null`  
- `sodium_mg: float | null`  
- `calcium_mg: float | null`  
DB 컬럼명은 모두 소문자로 변환되어 `FoodItem` 필드와 매핑됩니다.  
  
### 3-3. 응답
- 성공 시: HTTP 200, JSON 객체  
- 응답 데이터 예시)  
```JSON  
{
  "items": [
    {
      "calcium_mg": 6.0,
      "food_id": 707,
      "food_name": "쌀밥",
      "category": "밥류",
      "calories_kcal": 166.0,
      "carbs_g": 37.33,
      "protein_g": 3.36,
      "fat_g": 0.32,
      "sugar_g": 0.02,
      "fiber_g": 0.1,
      "sodium_mg": 0.0
    }
  ]
}
```  
- 실제 값은 Oracle DB `food_nutrition` 테이블의 데이터에 따라 달라질 수 있습니다.  
- 조회 결과가 없는 경우 `items`는 빈 배열이 될 수 있습니다.  
  
  
---  
  
## 4. Java(Spring) 연동 개요
### 4-1. 호출 방식
- 컨트롤러에서 `MultipartFile`을 입력으로 받습니다.  
- 서비스에서 `RestTemplate`(또는 `WebClient`)를 사용해 FastAPI 엔드포인트를 호출합니다.  
- 요청은 `multipart/form-data`로 전송합니다.  
  
### 4-2. 컨트롤러 예시
```java
// controller.java  
  
@Controller  public class TestController {    
    
    @Autowired    
    TestService testService;    
    
    @PostMapping("/test")    
    public String test(Model model,    
                       TestDto testDto){    
        MultipartFile file = testDto.getFile();    
    
        try {    
            testService.get_items(file);    
        }catch(Throwable e){    
            System.out.println(e.getMessage());    
        }    
    
        return "common/test";    
}  }  
```  
  
### 4-3. Service 예시
```java  
// TestService.java  
  
@Service  public class TestService {    
    private final RestTemplate restTemplate = new RestTemplate();    
    
    public void get_items(MultipartFile file) throws Throwable{    
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);    
    
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();    
        body.add("file", file.getResource());    
    
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);    
    
        String url = "http://localhost:8000/api_test";    
		ResponseEntity<ResultDto> response = restTemplate.postForEntity(url, request, ResultDto.class);  
		ResultDto result = response.getBody();  
		List<TestDto> items = result.getItems();
		
		// ... 이하 로직 구현
    }  }  
```  
  
### 4-4. DTO 예시
 (생성자/setter/getter/toString 생략)  
```java  
// ResultDto.java  
  public class ResultDto {    
private List<TestDto> items;  }  
  
  
// TestDto.java  
  
public class TestDto {    
    private int food_id;    
    private String food_name;    
    private String category;    
    private float calories_kcal;    
    private Float carbs_g;    
    private Float protein_g;    
    private Float fat_g;    
    private Float sugar_g;    
    private Float fiber_g;    
    private Float sodium_mg;    
    private Float calcium_mg;  
    
	private MultipartFile file;
}
```  
- FastAPI 응답 JSON 키(`food_id`, `food_name`, `category`, ...)와 DTO 필드명이 매핑됩니다.  
- 필드명은 FastAPI `FoodItem` 스키마 및 DB 컬럼명과 동일해야 직렬화/역직렬화가 정상 동작합니다.  
- `file` 필드는 업로드용으로 사용하며, 응답에는 포함되지 않습니다.  
  
  
---  
  
## 5. 전체 동작 흐름 요약
  
1. 클라이언트(웹/앱)에서 음식 사진을 선택해 Spring `/test` 엔드포인트로 업로드합니다.  
2. `TestController`에서 전달받은 `MultipartFile`을 서비스로 넘깁니다.  
3. `TestService`에서  
    - `multipart/form-data` 요청 바디를 구성하고  
   - `POST http://localhost:8000/api_test`로 FastAPI 서버를 호출합니다.  
4. FastAPI 서버에서  
   - 파일 유효성 검사(존재 여부, MIME 타입, 빈 파일 여부)를 수행하고  
   - YOLO 모델로 객체 감지를 수행 후 결과를 콘솔에 출력합니다.  
   - 검출된 음식명 리스트를 DB 기준으로 매핑 후, Oracle `food_nutrition` 테이블을 조회합니다.  
   - 조회된 행들을 `FoodItem` 리스트로 변환해 `items` 배열로 응답합니다.  
1. Spring 서비스에서 응답을 `ResultDto` → `List<TestDto>`로 역직렬화하고, 후처리를 수행합니다.  

  
---  
  
## 6. 현재 상태
  
- 이미지 업로드 → YOLO 객체 감지 → Oracle 영양 DB 조회까지의 파이프라인이 구성돼 있습니다.  
- YOLO 감지 결과를 바탕으로 DB 조회 후, 응답을 반환합니다.  
- YOLO model은 중간 학습 결과물이며 변경될 수 있습니다.
- `food_nutrition` 테이블 스키마에 따라 `FoodItem`/`TestDto` 필드 구성이 맞아야 합니다.  
  
---  
  
## 7. 동작 예

  ![설명](./asset/example.gif)
