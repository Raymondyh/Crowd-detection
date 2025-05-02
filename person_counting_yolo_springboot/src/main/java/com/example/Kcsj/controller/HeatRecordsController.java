package com.example.Kcsj.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Kcsj.common.Result;
import com.example.Kcsj.entity.HeatRecords;
import com.example.Kcsj.mapper.HeatRecordsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 热力记录控制器
 * <p>
 * 处理与热力记录相关的HTTP请求，提供以下功能：
 * <ul>
 *   <li>查询所有热力记录</li>
 *   <li>根据ID查询单个热力记录</li>
 *   <li>分页查询热力记录（支持按用户名、开始时间、设备型号模糊搜索）</li>
 *   <li>新增热力记录</li>
 *   <li>更新热力记录</li>
 *   <li>删除热力记录</li>
 * </ul>
 *
 * 使用MyBatis-Plus进行数据库操作，返回统一封装的Result对象。
 *
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */

@RestController
//处理 HTTP 请求的控制器，专用于 REST API
@RequestMapping("/heatrecords")
public class HeatRecordsController {
    @Resource
    // 注入热力记录数据访问层接口
    HeatRecordsMapper heatRecordsMapper;
    /**
     * 查询所有热力记录
     *
     * @return 包含所有热力记录的Result对象
     */

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(heatRecordsMapper.selectList(null));
    }
    /**
     * 根据ID查询单个热力记录
     *
     * @param id 热力记录ID
     * @return 包含指定热力记录的Result对象
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(heatRecordsMapper.selectById(id));
    }

    /**
     * 分页查询热力记录（支持模糊搜索）
     *
     * @param pageNum  当前页码（默认为1）
     * @param pageSize 每页记录数（默认为10）
     * @param search   用户名模糊搜索条件
     * @param search1  开始时间模糊搜索条件
     * @param search2  设备型号模糊搜索条件
     * @return 包含分页热力记录的Result对象
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2) {
        // 构建Lambda查询条件
        LambdaQueryWrapper<HeatRecords> wrapper = Wrappers.<HeatRecords>lambdaQuery();
        // 按开始时间降序排序
        wrapper.orderByDesc(HeatRecords::getStartTime);

        // 添加模糊搜索条件
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(HeatRecords::getUsername, search); // 按用户名搜索
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(HeatRecords::getStartTime, search1); // 按开始时间搜索
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(HeatRecords::getModel, search2); // 按设备型号搜索
        }
        // 执行分页查询
        Page<HeatRecords> Page = heatRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    /**
     * 删除热力记录
     *
     * @param id 要删除的热力记录ID
     * @return 操作成功的Result对象
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        heatRecordsMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 更新热力记录
     *
     * @param heatRecords 包含更新信息的热力记录对象
     * @return 操作成功的Result对象
     */
    @PostMapping("/update")
    public Result<?> updates(@RequestBody HeatRecords heatRecords) {
        heatRecordsMapper.updateById(heatRecords);
        return Result.success();
    }


    /**
     * 新增热力记录
     *
     * @param heatRecords 要新增的热力记录对象
     * @return 操作成功的Result对象
     */
    @PostMapping
    public Result<?> save(@RequestBody HeatRecords heatRecords) {
        heatRecordsMapper.insert(heatRecords);
        return Result.success();
    }
}
