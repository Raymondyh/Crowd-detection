package com.example.Kcsj.controller;

import com.example.Kcsj.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * 预测结果控制器
 * <p>
 * 该控制器负责与Python Flask服务进行交互，获取预测相关的数据。
 * 主要功能包括：
 * <ul>
 *   <li>获取文件名列表</li>
 * </ul>
 *
 * 使用RestTemplate作为HTTP客户端，与Flask服务进行通信。
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */

@RestController
@RequestMapping("/flask")
public class PredictionController {
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * 获取文件名列表
     *
     * 通过调用Flask服务的/file_names接口，获取文件名列表数据。
     *
     * @return 包含文件名列表的Result对象
     *         成功时返回Flask服务的响应数据
     *         失败时返回错误信息
     */

    @GetMapping("/file_names")
    public Result<?> getFileNames() {
        try {
            // 调用 Flask API 获取文件名列表
            // 注意：这里假设Flask服务运行在本地5000端口
            String response = restTemplate.getForObject("http://127.0.0.1:5000/file_names", String.class);
            // 返回成功结果
            return Result.success(response);
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            // 在生产环境中，建议记录异常日志
            return Result.error("-1", "Error: " + e.getMessage());
        }
    }
}
