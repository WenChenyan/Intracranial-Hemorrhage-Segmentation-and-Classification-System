package com.example.springboot.controller.DeepLearningModelPredict;

import com.example.springboot.common.Result;
import com.example.springboot.controller.DeepLearningModelPredict.request.ModelRequest;
import com.example.springboot.service.IModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin //解决跨域问题
@RestController
@RequestMapping("/model")
public class ModelController {
    @Autowired
    IModelService modelService;

    @GetMapping("/list")
    public Result listByTask(@RequestParam(value = "task") String task,
                             @RequestParam(value = "modelName", required = false) String modelName
                             )
    {
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setTask(task);
        modelRequest.setModelName(modelName);
        return Result.success(modelService.listByCondition(modelRequest));
    }
}
