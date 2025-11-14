# 이미지 기반 식단 분석 test API

업로드된 음식 사진 파일을 FastAPI 서버로 전송하고, 서버에서 더미 영양정보를 응답으로 주는 테스트용 API입니다.  
현재는 분석 로직 없이 고정된 더미 데이터만 반환합니다.

---

## 1. 구성 개요

- 백엔드 서버: Python FastAPI (`main.py`)
- 엔드포인트: `POST /api_test`
- 요청 형식: `multipart/form-data` 이미지 파일 1개
- 응답 형식: 음식 정보 리스트(JSON)
- 클라이언트 예시: Java(Spring)에서 `RestTemplate`로 호출

---

## 2. 서버 실행 준비

### 2-1. 의존성 설치

`requirements.txt` 기준으로 패키지 설치를 진행합니다.

```bash
# (선택) 가상환경 생성
python -m venv venv

# 가상환경 활성화 (OS에 따라 택 1)
# Windows
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

```
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
INFO:     Started reloader process [19924] using WatchFiles
INFO:     Started server process [13720]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
```
위와 같은 메세지가 뜨면 성공

---

## 3. FastAPI 엔드포인트 설명 (`main.py`)

### 3-1. 요청
- Method: `POST`
- URL: `http://localhost:8000/api_test`
- Content-Type: `multipart/form-data`
- Form 필드
    - `file`: 이미지 파일
        - 허용 확장자: `image/png`, `image/jpeg`

### 3-2. 응답
- 성공 시: HTTP 200, JSON 배열
- 응답 데이터 예시)
```JSON
{
  "name": "김밥 1줄",
  "kcal": 450,
  "carbohydrate": 55,
  "protein": 7,
  "fat": 8
}
```

에러 예시)
- 400 `"파일 없음"`
- 400 `"잘못된 파일 유형"`
- 400 `"빈 파일"`

## 4. Java(Spring) 연동 예시

### 4-1. 컨트롤러 예시
```java
// controller.java

@Controller  
public class TestController {  
  
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
    }  
}
```

### 4-2. Service 예시
```java
// TestService.java

@Service  
public class TestService {  
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
        System.out.println(items);  
    }  
}
```

### 4-3. DTO 예시 (생성자/setter/getter 생략)
```java
// ResultDto.java
  
public class ResultDto {  
    private List<TestDto> items;  
}


// TestDto.java

public class TestDto {
    private String name;
    private float kcal;
    private float carbohydrate;
    private float protein;
    private float fat;
    private MultipartFile file;
}
```
- FastAPI 응답 JSON 키(`name`, `kcal`, `carbohydrate`, `protein`, `fat`)와 DTO 필드명이 매핑됩니다.
- `file` 필드는 업로드용으로 사용하며, 응답에는 포함되지 않습니다.

---

## 5. 동작 흐름 요약

1. 클라이언트(웹/앱)에서 음식 사진을 선택해 `/test`로 업로드합니다.
2. `TestController`에서 전달받은 파일을 `testService.get_items(file)`을 호출합니다.
3. `TestService.get_items`에서
    - `multipart/form-data` 형식으로 요청 바디를 구성하고
    - `RestTemplate`을 사용해 FastAPI 서버의 `POST http://localhost:8000/api_test`를 호출합니다.
4. FastAPI 서버에서
    - 파일 유효성 검사(존재 여부, MIME 타입, 빈 파일 여부)를 수행하고
    - 업로드된 파일을 `test.png`로 저장한 뒤
    - 더미 음식 정보 리스트(JSON)를 응답으로 반환합니다.
5.  `TestService`에서 응답을 `ResultDto`로 역직렬화하고, 내부의 `items`를 콘솔에 출력합니다.

---

## 6. 현재 상태

- 실제 이미지 분석/영양소 인식 기능은 구현되어 있지 않습니다.
- 항상 고정된 더미 데이터(김밥, 떡볶이)를 응답으로 반환합니다.
- 반환은 응답 형식 예시로 작성된 것으로 변경될 수 있습니다.
- 파일 저장(`test.png`)은 업로드 파일 확인용입니다.


## 7. 동작 예

`[TestDto{carbohydrate=0.0, name='김밥 1줄', kcal=450.0, protein=7.0, fat=8.0, file=null}, TestDto{carbohydrate=0.0, name='떡볶이', kcal=459.0, protein=9.0, fat=2.0, file=null}]`
`jpeg`/`jpg`/`png` 파일을 `POST` 했을 때, 콘솔에 위 메세지가 출력되면 성공
