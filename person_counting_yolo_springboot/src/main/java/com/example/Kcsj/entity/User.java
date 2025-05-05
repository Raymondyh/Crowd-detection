package com.example.Kcsj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体类
 * <p>
 * 对应数据库表 user，用于存储用户相关信息。
 * 包含用户的基本信息、认证信息和元数据。
 * </p>
 *
 * <p>
 * 主要字段说明：
 * <ul>
 *   <li>id - 用户ID，主键，自增</li>
 *   <li>username - 用户名，唯一标识用户</li>
 *   <li>password - 用户密码（建议加密存储）</li>
 *   <li>name - 用户真实姓名</li>
 *   <li>sex - 用户性别</li>
 *   <li>email - 用户电子邮箱</li>
 *   <li>tel - 用户联系电话</li>
 *   <li>role - 用户角色（如admin/common）</li>
 *   <li>avatar - 用户头像URL</li>
 *   <li>time - 用户创建时间/注册时间</li>
 * </ul>
 * </p>
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */


@TableName("user")  // MyBatis-Plus: 指定表名
@Data              // Lombok: 自动生成 getter/setter/toString/equals/hashCode
@Builder           // Lombok: 生成建造者模式
@AllArgsConstructor  // Lombok: 全参构造器
@NoArgsConstructor   // Lombok: 无参构造器
public class User {
    /**
     * 用户ID
     * <p>
     * 主键，自增
     * </p>
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String sex;
    private String email;
    private String tel;
    private String role;
    private String avatar;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    // ======== Getter/Setter 方法 ========
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
