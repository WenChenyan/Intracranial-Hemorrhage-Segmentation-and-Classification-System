package com.example.springboot.controller.DeepLearningModelPredict.request;

import lombok.Data;

import java.util.List;

@Data
public class DetectRequest {
    private Boolean existNii;
    private String segModelName;
    private String clsModelName;
    private List<String> filesUrl;
    private String patientName;
    private String patientSex;
    private String patientInfo;
}
