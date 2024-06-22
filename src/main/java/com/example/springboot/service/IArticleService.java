package com.example.springboot.service;

import com.example.springboot.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IArticleService extends IService<Article> {

    List<Article> getHot();

    Integer getCount();
}
