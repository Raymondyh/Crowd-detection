package com.example.Kcsj.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Kcsj.common.Result;
import com.example.Kcsj.entity.User;
import com.example.Kcsj.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 用户控制器
 * <p>
 * 处理与用户相关的HTTP请求，提供以下功能：
 * <ul>
 *   <li>用户分页列表查询（支持按用户名模糊搜索）</li>
 *   <li>根据用户名查询用户信息</li>
 *   <li>查询所有用户</li>
 *   <li>用户登录验证</li>
 *   <li>用户注册</li>
 *   <li>更新用户信息</li>
 *   <li>删除用户</li>
 *   <li>新增用户（备用接口）</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用MyBatis-Plus进行数据库操作，返回统一封装的Result对象。
 * 登录功能包含密码验证和用户名存在性检查。
 * 注册功能会自动填充默认用户信息（如姓名、性别、角色等）。
 * </p>
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 用户数据访问层接口
     * 用于执行与用户相关的数据库操作
     */
    @Resource
    UserMapper userMapper;

    /**
     * 用户分页列表查询，支持按用户名模糊搜索
     *
     * @param pageNum  当前页码（默认为1）
     * @param pageSize 每页记录数（默认为10）
     * @param search   用户名模糊搜索条件
     * @return 包含分页用户数据的Result对象
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        // 构建Lambda查询条件
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery();
        // 默认按ID降序排序
        wrapper.orderByDesc(User::getId);
        // 添加用户名模糊搜索条件
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(User::getUsername, search);
        }
        // 执行分页查询
        Page<User> UserPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(UserPage);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 包含用户信息的Result对象
     */
    @GetMapping("/{username}")
    public Result<?> getByUsername(@PathVariable String username) {
        System.out.println(username);
        return Result.success(userMapper.selectByUsername(username));
    }

    /**
     * 查询所有用户
     *
     * @return 包含所有用户数据的Result对象
     */
    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(userMapper.selectList(null));
    }

    /**
     * 用户登录验证
     *
     * @param userParam 包含用户名和密码的用户对象
     * @return 登录成功返回用户信息，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User userParam) {
        System.out.println(userParam); // 打印登录参数用于调试（生产环境建议移除）
        try{
            // 根据用户名查询用户信息
            User userPwd = userMapper.selectByName(userParam.getUsername());
            // 构建查询条件（实际未使用，可优化）
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userParam.getUsername());
            queryWrapper.eq("password", userPwd.getPassword());
            User res = userMapper.selectOne(queryWrapper);
            //  判断密码是否正确
            if (!Objects.equals(userParam.getPassword(), userPwd.getPassword())) {
                return Result.error("-1", "密码错误！");
            } else {
                return Result.success(res);
            }
        }catch (Exception e){
            // 用户名不存在时捕获异常
            return Result.error("-1", "用户名不存在！");
        }
    }

    /**
     * 用户注册
     *
     * @param user 包含用户名和密码的用户对象
     * @return 注册成功返回空Result对象，失败返回错误信息
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        // 检查用户名是否已存在
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
        if (res != null) {
            return Result.error("-1", "用户名重复");
        }

        // 创建新用户并填充默认信息
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword());
        newUser.setName("张三");      // 默认姓名
        newUser.setSex("男");        // 默认性别
        newUser.setRole("common");   // 默认角色
        newUser.setEmail("123@qq.com"); // 默认邮箱
        newUser.setTime(new Date());  // 当前时间
        newUser.setTel("1234567889"); // 默认电话
        newUser.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        // 插入新用户
        userMapper.insert(newUser);
        return Result.success();
    }

    /**
     * 更新用户信息
     *
     * @param user 包含更新信息的用户对象
     * @return 操作成功的Result对象
     */
    @PostMapping("/update")
    public Result<?> updates(@RequestBody User user) {
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        userMapper.deleteById(id);
        return Result.success();
    }
    @PostMapping
    public Result<?> save(@RequestBody User user) {
        userMapper.insert(user);
        return Result.success();
    }
}
