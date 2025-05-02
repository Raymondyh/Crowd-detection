package com.example.Kcsj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 热力记录实体类
 * <p>
 * 对应数据库表 heatrecords，用于存储热成像设备采集的监控数据记录。
 * 包含设备信息、视频路径、统计人数等核心字段。
 *
 * 各字段的get和set
 *
 * <p>主要字段说明：
 * <ul>
 *   <li>id - 主键，自增</li>
 *   <li>model - 设备型号，标识数据来源设备</li>
 *   <li>videoPath - 原始视频存储路径</li>
 *   <li>uploadedUrl - 处理后的视频访问地址</li>
 * </ul>
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */

@TableName("heatrecords")  // MyBatis-Plus: 指定表名
@Data                      // Lombok: 自动生成 getter/setter/toString/equals/hashCode
@Builder                   // Lombok: 生成建造者模式
@AllArgsConstructor        // Lombok: 全参构造器
@NoArgsConstructor         // Lombok: 无参构造器
public class HeatRecords {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String model;         // 对应数据库字段 model（模型信息）
    private String videoPath;     // 对应数据库字段 video_path（视频路径）
    private String uploadedUrl;   // 对应数据库字段 uploaded_url（上传后的URL地址）
    private String username;      // 对应数据库字段 username（用户名）
    private String startTime;     // 对应数据库字段 start_time（开始时间）
    private String height;        // 对应数据库字段 height（高度）
    private String inCount;       // 对应数据库字段 in_count（进入人数）
    private String outCount;      // 对应数据库字段 out_count（离开人数）

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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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
