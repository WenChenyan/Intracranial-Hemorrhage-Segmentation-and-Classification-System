package com.example.springboot.service.impl;

import com.example.springboot.entity.Article;
import com.example.springboot.mapper.ArticleMapper;
import com.example.springboot.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Resource
    ArticleMapper articleMapper;
    @Override
    public List<Article> getHot() {
        return articleMapper.getHot();
    }

    @Override
    public Integer getCount() {
        return articleMapper.getCount();
    }
}
