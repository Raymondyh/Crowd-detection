# -*- coding: utf-8 -*-
# @Time : 2024-11-15 10:14
# @Author : 林枫
# @File : flaskR.py

import json
import os
import time
import requests
from flask import Flask, Response, request, jsonify
from personDetect import PersonDetect
from heatDetect import HeatDetect
from flask_socketio import SocketIO, emit


# Flask 应用设置
class VideoProcessingApp:
    def __init__(self, host='0.0.0.0', port=5000):
        """初始化 Flask 应用并设置路由"""
        self.app = Flask(__name__)
        self.socketio = SocketIO(self.app, cors_allowed_origins="*")  # 初始化 SocketIO
        self.host = host
        self.port = port
        self.setup_routes()
        self.path = './runs/video/download.mp4'

    def setup_routes(self):
        """设置所有路由"""
        self.app.add_url_rule('/file_names', 'file_names', self.file_names, methods=['GET'])
        self.app.add_url_rule('/person_count', 'person_count', self.person_count)
        self.app.add_url_rule('/heat_count', 'heat_count', self.heat_count)

        # 添加 WebSocket 事件
        @self.socketio.on('connect')
        def handle_connect():
            print("WebSocket connected!")
            emit('message', {'data': 'Connected to WebSocket server!'})

        @self.socketio.on('disconnect')
        def handle_disconnect():
            print("WebSocket disconnected!")

    def run(self):
        """启动 Flask 应用"""
        self.socketio.run(self.app, host=self.host, port=self.port, allow_unsafe_werkzeug=True)

    def get_file_names(self, directory):
        """获取指定文件夹中的所有文件名"""
        try:
            # 列出目录中的所有文件和文件夹
            files = os.listdir(directory)
            # 过滤出文件（排除文件夹）
            file_names = [file for file in files if os.path.isfile(os.path.join(directory, file))]
            return file_names
        except Exception as e:
            print(f"发生错误: {e}")
            return []

    def upload_video(self, out_path):
        """
        上传处理后的视频文件到远程服务器
        """
        upload_url = "http://localhost:9999/files/upload"  # 远程上传接口
        try:
            # 打开视频文件并上传
            with open(out_path, 'rb') as video_file:
                files = {'file': (os.path.basename(out_path), video_file)}
                response = requests.post(upload_url, files=files)

                if response.status_code == 200:
                    print("视频上传成功！")
                    # 返回远程服务器的文件 URL
                    uploaded_url = response.json()['data']
                    print(f"上传视频 URL: {uploaded_url}")
                    return uploaded_url
                else:
                    print("视频上传失败！")
        except Exception as e:
            print(f"上传视频时发生错误: {str(e)}")

    def download_video(self, url, save_path):
        """
        下载视频文件并保存到指定路径
        :param url: 视频下载链接
        :param save_path: 保存完整路径（包括文件名）
        """
        # 确保保存目录存在
        save_dir = os.path.dirname(save_path)
        os.makedirs(save_dir, exist_ok=True)

        try:
            # 发送GET请求获取视频内容
            with requests.get(url, stream=True) as response:
                response.raise_for_status()  # 检查请求是否成功
                with open(save_path, 'wb') as file:
                    for chunk in response.iter_content(chunk_size=8192):  # 分块写入
                        if chunk:
                            file.write(chunk)
            print(f"视频已成功下载并保存到 {save_path}")
        except requests.RequestException as e:
            print(f"下载失败: {e}")

    def file_names(self):
        """模型列表接口"""
        model_items = [name for name in self.get_file_names("./model")]
        # 转换为所需格式
        formatted_model_items = [{'value': item, 'label': item} for item in model_items]
        # 创建字典
        result = {
            'model_items': formatted_model_items,
        }

        # 转换为 JSON 字符串
        json_result = json.dumps(result)
        print(json_result)
        return json_result

    def person_count(self):
        """视频流处理接口"""
        # 获取查询参数
        username = request.args.get('username')
        height = request.args.get('height')
        model = request.args.get('model')
        video_path = request.args.get('video')
        start_time = request.args.get('time')

        # 打印接收到的参数
        print(
            f"Received params: username={username}, height={height}, model={model}, video={video_path}, time={start_time}")

        if not video_path:
            return jsonify({"error": "Missing 'video' parameter"}), 400

        # 创建 Detect 实例并传入视频路径
        model_path = './model/' + model
        self.download_video(video_path, self.path)
        detector = PersonDetect(model_path, self.path, height, False)
        # 通知前端任务完成
        self.socketio.emit('message', {'data': '加载完成，开始处理！'})
        # 启动视频处理
        detector.start_processing()

        def generate():
            """生成视频流并处理帧"""
            while not detector.finished_event.is_set():
                frame = detector.get_frame()
                if frame:
                    yield (b'--frame\r\n'
                           b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
                time.sleep(0.01)
            print("视频流生成完成！")
            inCount = detector.counter.in_count
            outCount = detector.counter.out_count
            # 通知前端任务完成
            self.socketio.emit('message', {'data': '解析完成，正在保存！'})
            # 转换并实时获取进度
            out_path = ''
            for progress in detector.convert_avi_to_mp4():
                if isinstance(progress, float):  # 判断是否是进度百分比
                    self.socketio.emit('progress', {'data': progress})
                elif isinstance(progress, str):  # 判断是否是最终路径
                    self.socketio.emit('progress', {'data': 100})
                    out_path = progress
            uploadedUrl = self.upload_video(out_path)
            if os.path.exists(out_path):
                os.remove(out_path)
            data = {
                "username": username,
                "height": height,
                "model": model,
                "videoPath": video_path,
                "startTime": start_time,
                "uploadedUrl": uploadedUrl,
                "inCount": inCount,
                "outCount": outCount
            }
            data = json.dumps(data)
            self.save_data(data, "http://localhost:9999/personrecords")  # 在结束后调用额外功能
            if os.path.exists(self.path):
                os.remove(self.path)

        return Response(generate(), mimetype='multipart/x-mixed-replace; boundary=frame')

    def heat_count(self):
        """视频流处理接口"""
        # 获取查询参数
        username = request.args.get('username')
        model = request.args.get('model')
        video_path = request.args.get('video')
        start_time = request.args.get('time')
        height = request.args.get('height')
        show_in = request.args.get('show_in')
        show_out = request.args.get('show_out')
        if show_in == 'False':
            show_in = False
            show_out = False
        else:
            show_in = True
            show_out = True

        if not video_path:
            return jsonify({"error": "Missing 'video' parameter"}), 400

        # 创建 Detect 实例并传入视频路径
        model_path = './model/' + model
        self.download_video(video_path, self.path)
        detector = HeatDetect(model_path, self.path, height, False, show_in=show_in, show_out=show_out)
        # 通知前端任务完成
        self.socketio.emit('message', {'data': '加载完成，开始处理！'})
        # 启动视频处理
        detector.start_processing()

        def generate():
            """生成视频流并处理帧"""
            while not detector.finished_event.is_set():
                frame = detector.get_frame()
                if frame:
                    yield (b'--frame\r\n'
                           b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
                time.sleep(0.01)
            print("视频流生成完成！")
            # 通知前端任务完成
            self.socketio.emit('message', {'data': '解析完成，正在保存！'})
            # 转换并实时获取进度
            out_path = ''
            for progress in detector.convert_avi_to_mp4():
                if isinstance(progress, float):  # 判断是否是进度百分比
                    self.socketio.emit('progress', {'data': progress})
                elif isinstance(progress, str):  # 判断是否是最终路径
                    self.socketio.emit('progress', {'data': 100})
                    out_path = progress
            uploadedUrl = self.upload_video(out_path)
            if os.path.exists(out_path):
                os.remove(out_path)

            if show_in:
                data = {
                    "username": username,
                    "height": height,
                    "model": model,
                    "videoPath": video_path,
                    "startTime": start_time,
                    "uploadedUrl": uploadedUrl,
                    "inCount": detector.heatmap.in_count,
                    "outCount": detector.heatmap.out_count
                }
            else:
                data = {
                    "username": username,
                    "height": '无',
                    "model": model,
                    "videoPath": video_path,
                    "startTime": start_time,
                    "uploadedUrl": uploadedUrl,
                    "inCount": '无',
                    "outCount": '无',
                }
            data = json.dumps(data)
            self.save_data(data, "http://localhost:9999/heatrecords")  # 在结束后调用额外功能
            if os.path.exists(self.path):
                os.remove(self.path)

        return Response(generate(), mimetype='multipart/x-mixed-replace; boundary=frame')

    def save_data(self, data, post_url):
        """
        将结果数据上传到服务器
        """
        headers = {'Content-Type': 'application/json'}
        try:
            response = requests.post(post_url, data=data, headers=headers)
            print(data)
            if response.status_code == 200:
                print("记录上传成功！")
            else:
                print(f"记录上传失败，状态码: {response.status_code}")
        except requests.RequestException as e:
            print(f"上传记录时发生错误: {str(e)}")


# 启动应用
if __name__ == '__main__':
    video_app = VideoProcessingApp()
    video_app.run()
