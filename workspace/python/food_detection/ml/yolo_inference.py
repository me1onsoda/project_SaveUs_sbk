from ml import MODEL_PATH

from six import BytesIO
from ultralytics import YOLO
from collections import Counter
from PIL import Image

model = YOLO(MODEL_PATH / "saveUs_food_detection.pt")
model.to("cpu")

async def detect_objects(content: bytes) -> Counter:
    img = Image.open(BytesIO(content))
    pred = model.predict(img)[0]

    return Counter(p["name"] for p in pred.summary())
