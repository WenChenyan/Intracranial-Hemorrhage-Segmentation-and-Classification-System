package com.example.springboot.service;

import com.example.springboot.controller.DeepLearningModelPredict.request.DetectRequest;

public interface IImageService {
    String detectInQueue(DetectRequest detectRequest) throws Exception;
}
