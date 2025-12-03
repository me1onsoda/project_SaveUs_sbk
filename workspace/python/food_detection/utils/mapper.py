import csv
from utils import PATH

label_map = dict(csv.reader(open(PATH / "label_map.csv", encoding="utf-8")))
label_map.pop("old")
