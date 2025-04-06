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

@RestController
@RequestMapping("/personrecords")
public class PersonRecordsController {
    @Resource
    PersonRecordsMapper personRecordsMapper;

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(personRecordsMapper.selectList(null));
    }
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(personRecordsMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2) {
        LambdaQueryWrapper<PersonRecords> wrapper = Wrappers.<PersonRecords>lambdaQuery();
        wrapper.orderByDesc(PersonRecords::getStartTime);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(PersonRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(PersonRecords::getStartTime, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(PersonRecords::getModel, search2);
        }
        Page<PersonRecords> Page = personRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        personRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody PersonRecords personRecords) {
        personRecordsMapper.updateById(personRecords);
        return Result.success();
    }


    @PostMapping
    public Result<?> save(@RequestBody PersonRecords personRecords) {
        System.out.println(personRecords);
        personRecordsMapper.insert(personRecords);
        return Result.success();
    }
}
