package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Files;
import com.example.springboot.mapper.FileMapper;
import com.example.springboot.service.IFileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImmpl extends ServiceImpl<FileMapper, Files> implements IFileService {

}
