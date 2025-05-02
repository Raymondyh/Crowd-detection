package com.example.Kcsj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员记录实体类
 * <p>
 * 对应数据库表 personrecords，用于存储人员监控设备采集的监控数据记录。
 * 包含设备信息、视频路径、统计人数等核心字段。
 *
 * 各字段的get和set
 *
 * <p>主要字段说明：
 * <ul>
 *   <li>id - 主键，自增</li>
 *   <li>model - 设备型号，标识使用的模型型号</li>
 *   <li>height - 人员高度信息</li>
 *   <li>videoPath - 原始视频存储路径</li>
 *   <li>uploadedUrl - 处理后的视频访问地址</li>
 *   <li>username - 操作用户名</li>
 *   <li>startTime - 监控开始时间</li>
 *   <li>inCount - 进入人数统计</li>
 *   <li>outCount - 离开人数统计</li>
 * </ul>
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */

@TableName("personrecords")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonRecords {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String model;
    private String height;
    private String videoPath;
    private String uploadedUrl;
    private String username;
    private String startTime;
    private String inCount;
    private String outCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }


    public String getUploadedUrl() {
        return uploadedUrl;
    }

    public void setUploadedUrl(String uploadedUrl) {
        this.uploadedUrl = uploadedUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getInCount() {
        return inCount;
    }

    public void setInCount(String inCount) {
        this.inCount = inCount;
    }

    public String getOutCount() {
        return outCount;
    }

    public void setOutCount(String outCount) {
        this.outCount = outCount;
    }
}
