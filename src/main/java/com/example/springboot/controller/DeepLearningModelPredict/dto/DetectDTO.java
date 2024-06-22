package com.example.springboot.controller.DeepLearningModelPredict.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetectDTO {
    private Boolean existModel;
    private List<String> modelUrl;
    private List<String> segUrl;
    private List<List<Double>> clsProb ;
}
