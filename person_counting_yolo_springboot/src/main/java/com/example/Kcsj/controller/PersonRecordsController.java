package com.example.Kcsj.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Kcsj.common.Result;
import com.example.Kcsj.entity.PersonRecords;
import com.example.Kcsj.mapper.PersonRecordsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 人员记录控制器
 * <p>
 * 处理与人员记录相关的HTTP请求，提供以下功能：
 * <ul>
 *   <li>查询所有人员记录</li>
 *   <li>根据ID查询单个人员记录</li>
 *   <li>分页查询人员记录（支持按用户名、开始时间、设备型号模糊搜索）</li>
 *   <li>新增人员记录</li>
 *   <li>更新人员记录</li>
 *   <li>删除人员记录</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用MyBatis-Plus进行数据库操作，返回统一封装的Result对象。
 * 分页查询支持按以下字段排序和筛选：
 * <ul>
 *   <li>默认按开始时间(startTime)降序排序</li>
 *   <li>支持按用户名(username)模糊搜索</li>
 *   <li>支持按开始时间(startTime)模糊搜索</li>
 *   <li>支持按设备型号(model)模糊搜索</li>
 * </ul>
 * </p>
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */

@RestController
@RequestMapping("/personrecords")
public class PersonRecordsController {
    /**
     * 人员记录数据访问层接口
     * 用于执行与人员记录相关的数据库操作
     */
    @Resource
    PersonRecordsMapper personRecordsMapper;

    /**
     * 查询所有人员记录
     *
     * @return 包含所有人员记录的Result对象
     */
    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(personRecordsMapper.selectList(null));
    }

    /**
     * 根据ID查询单个人员记录
     *
     * @param id 人员记录ID
     * @return 包含指定人员记录的Result对象
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(personRecordsMapper.selectById(id));
    }

    /**
     * 分页查询人员记录（支持模糊搜索）
     *
     * @param pageNum  当前页码（默认为1）
     * @param pageSize 每页记录数（默认为10）
     * @param search   用户名模糊搜索条件
     * @param search1  开始时间模糊搜索条件
     * @param search2  设备型号模糊搜索条件
     * @return 包含分页人员记录的Result对象
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2) {
        // 构建Lambda查询条件
        LambdaQueryWrapper<PersonRecords> wrapper = Wrappers.<PersonRecords>lambdaQuery();
        // 默认按开始时间降序排序
        wrapper.orderByDesc(PersonRecords::getStartTime);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(PersonRecords::getUsername, search); // 按用户名搜索
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(PersonRecords::getStartTime, search1); // 按开始时间搜索
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(PersonRecords::getModel, search2); // 按设备型号搜索
        }
        // 执行分页查询
        Page<PersonRecords> Page = personRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    /**
     * 删除人员记录
     *
     * @param id 要删除的人员记录ID
     * @return 操作成功的Result对象
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        personRecordsMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 更新人员记录
     *
     * @param personRecords 包含更新信息的人员记录对象
     * @return 操作成功的Result对象
     */
    @PostMapping("/update")
    public Result<?> updates(@RequestBody PersonRecords personRecords) {
        personRecordsMapper.updateById(personRecords);
        return Result.success();
    }


    /**
     * 新增人员记录
     *
     * @param personRecords 要新增的人员记录对象
     * @return 操作成功的Result对象
     */
    @PostMapping
    public Result<?> save(@RequestBody PersonRecords personRecords) {
        System.out.println(personRecords);
        personRecordsMapper.insert(personRecords);
        return Result.success();
    }
}
