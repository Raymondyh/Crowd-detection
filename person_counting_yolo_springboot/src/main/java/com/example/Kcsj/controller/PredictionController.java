package com.example.Kcsj.controller;

import com.example.Kcsj.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/flask")
public class PredictionController {
    private final RestTemplate restTemplate = new RestTemplate();


    @GetMapping("/file_names")
    public Result<?> getFileNames() {
        try {
            // 调用 Flask API
            String response = restTemplate.getForObject("http://127.0.0.1:5000/file_names", String.class);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("-1", "Error: " + e.getMessage());
        }
    }
}
