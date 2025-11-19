from six import BytesIO
from ultralytics import YOLO
from collections import Counter
from PIL import Image

# model = YOLO("saveUs_yolo.pt")

# 기본 yolo 모델
model = YOLO("yolov10s.pt")

def detect_objects(content: bytes) -> Counter:
    img = Image.open(BytesIO(content))

    result = model.predict(img)
    return Counter(i["name"] for i in result[0].summary())
