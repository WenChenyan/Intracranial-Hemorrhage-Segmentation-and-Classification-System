package com.example.springboot.controller.DeepLearningModelPredict.request;

import lombok.Data;

@Data
public class ModelRequest {
    private String modelName;
    private String task;
}
