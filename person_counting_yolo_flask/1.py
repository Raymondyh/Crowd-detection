import ultralytics
from ultralytics import solutions

# 检查版本（必须 ≥8.1.0）
print("Ultralytics版本:", ultralytics.__version__)

# 直接查看 ObjectCounter 类的源码方法
print("ObjectCounter的方法列表:", dir(solutions.ObjectCounter))