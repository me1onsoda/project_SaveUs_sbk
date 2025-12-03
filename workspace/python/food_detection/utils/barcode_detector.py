from __future__ import annotations

from pathlib import Path
from typing import List, Tuple, Sequence, Optional, Union, Set

import cv2
import numpy as np
from pyzbar.pyzbar import decode, ZBarSymbol


class BarcodeDetectorError(Exception):
    pass

class ImageLoadError(BarcodeDetectorError):
    pass


class BarcodeDetector:
    def __init__(
        self,
        symbols: Optional[Sequence[ZBarSymbol]] = None,
        max_width: int = 1920,
        max_height: int = 1920,
    ) -> None:
        if symbols is None:
            symbols = (
                ZBarSymbol.EAN13,
                ZBarSymbol.EAN8,
                ZBarSymbol.UPCA,
                ZBarSymbol.CODE128,
                ZBarSymbol.CODE39,
                ZBarSymbol.QRCODE,
            )

        self._symbols: Tuple[ZBarSymbol, ...] = tuple(symbols)
        self._max_width = int(max_width)
        self._max_height = int(max_height)

    def detect_codes_from_file(
        self,
        image: Union[np.ndarray, bytes, bytearray, memoryview],
    ) -> List[str]:
        img_array = self._load_image(image)
        img_array = self._resize_if_needed(img_array)
        return self.detect_codes_from_array(img_array)

    def detect_codes_from_array(self, image: np.ndarray) -> List[str]:
        preprocessed = self._preprocess(image)

        decoded_raw = []
        decoded_raw.extend(self._decode(image, note="original_bgr"))
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        decoded_raw.extend(self._decode(gray, note="gray"))
        decoded_raw.extend(self._decode(preprocessed, note="preprocessed"))

        return self._normalize_results(decoded_raw)

    def _load_image(
        self,
        image: Union[np.ndarray, bytes, bytearray, memoryview],
    ) -> np.ndarray:
        if isinstance(image, np.ndarray):
            return image

        if isinstance(image, (bytes, bytearray, memoryview)):
            buf = np.frombuffer(image, dtype=np.uint8)
            img = cv2.imdecode(buf, cv2.IMREAD_COLOR)
            if img is None:
                raise ImageLoadError()
            return img

    def _resize_if_needed(self, image: np.ndarray) -> np.ndarray:
        h, w = image.shape[:2]
        scale_w = self._max_width / float(w)
        scale_h = self._max_height / float(h)
        scale = min(scale_w, scale_h, 1.0)

        if scale >= 1.0:
            return image

        new_size = (int(w * scale), int(h * scale))
        return cv2.resize(image, new_size, interpolation=cv2.INTER_AREA)

    def _preprocess(self, image: np.ndarray) -> np.ndarray:
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
        gray = clahe.apply(gray)

        gray_blur = cv2.GaussianBlur(gray, (3, 3), 0)

        grad_x = cv2.Sobel(gray_blur, ddepth=cv2.CV_32F, dx=1, dy=0, ksize=3)
        grad_x = cv2.convertScaleAbs(grad_x)
        grad_x = cv2.normalize(grad_x, None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX)

        kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (21, 7))
        closed = cv2.morphologyEx(grad_x, cv2.MORPH_CLOSE, kernel)

        _, thresh = cv2.threshold(
            closed, 0, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU
        )

        thresh = cv2.erode(thresh, None, iterations=1)
        thresh = cv2.dilate(thresh, None, iterations=1)

        return thresh

    def _decode(self, image: np.ndarray, note: str = ""):
        try:
            results = decode(image, symbols=self._symbols)
            return results
        except Exception:
            return []

    def _normalize_results(self, raw_results) -> List[str]:
        codes: List[str] = []
        seen: Set[Tuple[str, bytes]] = set()

        for r in raw_results:
            key = (r.type, r.data)
            if key in seen:
                continue
            seen.add(key)

            data_str = r.data.decode("utf-8", errors="replace")
            codes.append(data_str)

        return codes



