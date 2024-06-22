package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.controller.DeepLearningModelPredict.request.ModelRequest;
import com.example.springboot.entity.Model;

import java.util.List;

public interface IModelService extends IService<Model> {
    List<Model> listByCondition(ModelRequest modelRequest);
}
