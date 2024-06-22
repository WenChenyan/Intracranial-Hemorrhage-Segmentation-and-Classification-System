package com.example.springboot.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.Validation;

public interface IValidationService extends IService<Validation> {
    void saveCode(String email, String code, Integer type, DateTime expireDate);

}
