from six import BytesIO
from ultralytics import YOLO
from collections import Counter
from PIL import Image

model = YOLO("saveUs_food_detection.pt")


async def detect_objects(content: bytes) -> Counter:
    img = Image.open(BytesIO(content))
    pred = model.predict(img)[0]

    # class 숫자로 return
    # return pred.boxes.cls.cpu().numpy().astype(int).tolist()

    # class 문자로 return
    return Counter(p["name"] for p in pred.summary())


if __name__ == "__main__":
    img = Image.open("C:/Users/240811/Desktop/sample/sample5.jpg")
    result = model.predict(img)[0]
    result.show()
