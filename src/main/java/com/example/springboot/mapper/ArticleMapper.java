package com.example.springboot.mapper;

import com.example.springboot.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface ArticleMapper extends BaseMapper<Article> {

    List<Article> getHot();

    Integer getCount();
}
