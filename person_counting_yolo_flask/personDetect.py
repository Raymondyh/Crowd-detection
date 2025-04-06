# -*- coding: utf-8 -*-
# @Time : 2024-11-20
# @Author : 林枫
# @File : personDetect.py

import os
import queue
import subprocess
import threading

import cv2
from ultralytics import solutions


class PersonDetect:
    """
    视频检测类，用于加载模型，处理视频，上传结果等。
    """
    def __init__(self, model_path, video_path, height, show=False, show_in=True, show_out=True):
        self.model_path = model_path
        self.video_path = video_path

        # 打开视频文件
        self.cap = cv2.VideoCapture(self.video_path)
        if not self.cap.isOpened():
            raise ValueError(f"无法打开视频文件: {self.video_path}")

        # 获取视频属性
        self.width = int(self.cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        self.height = int(self.cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        self.fps = int(self.cap.get(cv2.CAP_PROP_FPS))

        # 计算划线位置
        middle_height = int(height)
        self.line_points = [(0, middle_height), (self.width, middle_height)]

        # 输出路径和临时文件路径
        self.out_path = f'./runs/video/{self.get_filename_without_extension(video_path)}_output.mp4'
        self.temp_output = "./runs/video/temp_output.avi"

        # 视频写入器
        self.video_writer = cv2.VideoWriter(
            self.temp_output,
            cv2.VideoWriter_fourcc(*'XVID'),
            self.fps,
            (self.width, self.height)
        )

        # 初始化物体计数器
        self.counter = solutions.ObjectCounter(
            show=show,
            region=self.line_points,
            model=self.model_path,
            classes=[0],  # If you want to generate heatmap for specific classes i.e person and car.
            show_in=show_in,  # Display in counts
            show_out=show_out,  # Display out counts
        )

        # 帧队列
        self.frame_queue = queue.Queue(maxsize=10)

        # 多线程同步
        self.finished_event = threading.Event()

        # 视频处理线程
        self.processing_thread = None

    @staticmethod
    def get_filename_without_extension(filepath):
        """
        获取视频文件名（不含扩展名）
        """
        return os.path.splitext(os.path.basename(filepath))[0]

    def convert_avi_to_mp4(self):
        """
        使用 FFmpeg 将 AVI 格式转换为 MP4 格式，并显示转换进度。
        """
        ffmpeg_command = f"ffmpeg -i {self.temp_output} -vcodec libx264 {self.out_path}"
        process = subprocess.Popen(
            ffmpeg_command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        total_duration = self.get_video_duration()
        for line in process.stderr:
            if "time=" in line:
                try:
                    time_str = line.split("time=")[1].split(" ")[0]
                    h, m, s = map(float, time_str.split(":"))
                    processed_time = h * 3600 + m * 60 + s
                    if total_duration > 0:
                        progress = (processed_time / total_duration) * 100
                        print(f"FFmpeg Conversion Progress: {progress:.2f}%")
                        yield progress  # 实时返回当前进度
                except Exception:
                    pass

        process.wait()
        if os.path.exists(self.temp_output):
            os.remove(self.temp_output)
        yield 100  # 确保最后返回 100% 的进度
        yield self.out_path  # 最终返回输出文件路径

    def get_video_duration(self):
        """
        获取视频总时长（秒）
        """
        try:
            cap = cv2.VideoCapture(self.temp_output)
            if not cap.isOpened():
                return 0
            total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
            fps = cap.get(cv2.CAP_PROP_FPS)
            cap.release()
            return total_frames / fps if fps > 0 else 0
        except Exception:
            return 0

    def process_frame(self, frame):
        """
        处理单帧视频，返回处理后的帧
        """
        return self.counter.count(frame)


    def process_video(self):
        """
        读取并处理视频，生成处理后的视频帧
        """
        try:
            while self.cap.isOpened():
                ret, frame = self.cap.read()
                if not ret:
                    self.finished_event.set()
                    break

                processed_frame = self.process_frame(frame)
                ret, jpeg = cv2.imencode('.jpg', processed_frame)
                if ret and not self.frame_queue.full():
                    self.frame_queue.put(jpeg.tobytes())

                self.video_writer.write(processed_frame)
        except Exception as e:
            print(f"处理视频时发生错误: {str(e)}")
        finally:
            self.cap.release()
            self.video_writer.release()
            cv2.destroyAllWindows()

    def start_processing(self):
        """
        启动视频处理线程
        """
        if not self.processing_thread or not self.processing_thread.is_alive():
            self.processing_thread = threading.Thread(target=self.process_video)
            self.processing_thread.daemon = True
            self.processing_thread.start()

    def get_frame(self):
        """
        从队列中获取最新处理帧
        """
        return self.frame_queue.get() if not self.frame_queue.empty() else None

    def wait_for_finish(self):
        """
        等待视频处理完成
        """
        self.finished_event.wait()


if __name__ == '__main__':
    # 用户配置
    video_path = 'path_to_video_file'  # 替换为实际视频路径
    model_path = 'model/yolo11n.pt'  # 替换为实际模型路径
    height = '50'  # 中间划线高度

    # 创建检测对象
    detector = PersonDetect(model_path, video_path, height)

    # 启动视频处理
    detector.start_processing()

    # 等待处理完成
    detector.wait_for_finish()

    print(f"视频处理完成，结果保存至 {detector.out_path}")
