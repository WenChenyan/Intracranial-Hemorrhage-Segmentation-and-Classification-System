package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.controller.DeepLearningModelPredict.request.ModelRequest;
import com.example.springboot.entity.Model;
import com.example.springboot.mapper.ModelMapper;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.service.IModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ModelService extends ServiceImpl<ModelMapper,Model> implements IModelService {
    @Resource
    private ModelMapper modelMapper;

    @Override
    public List<Model> listByCondition(@RequestBody ModelRequest modelRequest) {
        QueryWrapper<Model>  queryWrapper = new QueryWrapper<>();
        if (!modelRequest.getModelName().isEmpty()){queryWrapper.eq("name",modelRequest.getModelName());}
        if (!modelRequest.getTask().isEmpty()){queryWrapper.eq("inform",modelRequest.getTask());}
        return modelMapper.selectList(queryWrapper);
    }
}
