package com.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import cn.hutool.core.date.Month;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.config.AuthAccess;
import com.example.springboot.entity.Article;
import com.example.springboot.entity.Files;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.FileMapper;
import com.example.springboot.service.IArticleService;
import com.example.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IArticleService articleService;

    @Resource
    private FileMapper fileMapper;

    @GetMapping("/article/count")
    public Result articleCount(){
        Integer count =  articleService.getCount();
        return Result.success(count);
    }

    @GetMapping("/article/month")
    public Result article(){
        List<Article> articles =  articleService.list();
        int[] month = new int[12];
        for (int i=0 ; i<12; i++){
            month[i]=0;
        }
        for (Article article:articles){
            Date time = article.getTime();
            int m = DateUtil.monthEnum(time).getValue();
            month[m] +=1;
        }
        return Result.success(month);
    }

    @GetMapping("/members")
    public Result members() {
        List<Integer> l1 = all();
        List<Integer> l2 = lawyer();
        List<Integer> l3 = user();
        return  Result.success(CollUtil.newArrayList(l1,l2,l3));
    }


    public List<Integer> all() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> list = userService.list(queryWrapper);
        return GetQuarter(list);
    }

    public List<Integer> lawyer() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "ROLE_LAWYER");
        List<User> list = userService.list(queryWrapper);
        return GetQuarter(list);
    }

    public List<Integer> user() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "ROLE_USER");
        List<User> list = userService.list(queryWrapper);
        return GetQuarter(list);
    }

    private List<Integer> GetQuarter(List<User> list){
        int q1 = 0; // 第一季度
        int q2 = 0; // 第二季度
        int q3 = 0; // 第三季度
        int q4 = 0; // 第四季度
        for (User user : list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter) {
                case Q1: q1 += 1; break;
                case Q2: q2 += 1; break;
                case Q3: q3 += 1; break;
                case Q4: q4 += 1; break;
                default: break;
            }
        }
        return CollUtil.newArrayList(q1, q2, q3, q4);
    }
}
