import csv

label_map = dict(csv.reader(open("label_map.csv", encoding="utf-8")))
label_map.pop("old")
