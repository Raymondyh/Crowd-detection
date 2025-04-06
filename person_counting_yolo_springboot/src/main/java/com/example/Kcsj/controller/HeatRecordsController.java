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

@RestController
@RequestMapping("/heatrecords")
public class HeatRecordsController {
    @Resource
    HeatRecordsMapper heatRecordsMapper;

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(heatRecordsMapper.selectList(null));
    }
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(heatRecordsMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2) {
        LambdaQueryWrapper<HeatRecords> wrapper = Wrappers.<HeatRecords>lambdaQuery();
        wrapper.orderByDesc(HeatRecords::getStartTime);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(HeatRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(HeatRecords::getStartTime, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(HeatRecords::getModel, search2);
        }
        Page<HeatRecords> Page = heatRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        heatRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody HeatRecords heatRecords) {
        heatRecordsMapper.updateById(heatRecords);
        return Result.success();
    }


    @PostMapping
    public Result<?> save(@RequestBody HeatRecords heatRecords) {
        heatRecordsMapper.insert(heatRecords);
        return Result.success();
    }
}
