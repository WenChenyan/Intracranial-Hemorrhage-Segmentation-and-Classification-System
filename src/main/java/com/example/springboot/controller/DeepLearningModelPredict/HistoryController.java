package com.example.springboot.controller.DeepLearningModelPredict;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.controller.DeepLearningModelPredict.dto.DetectDTO;
import com.example.springboot.entity.History;
import com.example.springboot.service.IHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin //解决跨域问题
@RestController
@RequestMapping("/history")
public class HistoryController {
    @Autowired
    IHistoryService historyService;


    @GetMapping("/{id}")
    public Result getOne(@PathVariable Integer id){
        History one = historyService.getById(id);
        DetectDTO detectDTO = new DetectDTO();
        detectDTO.setExistModel(one.getExistModel());
        if (one.getExistModel()){
            detectDTO.setModelUrl(Arrays.stream(one.getModelUrl().split("#")).collect(Collectors.toList()));
        }
        detectDTO.setSegUrl(Arrays.stream(one.getSegUrl().split("#")).collect(Collectors.toList()));
        List<String> probs=Arrays.stream(one.getClsProb().split("#")).collect(Collectors.toList());
        List<List<Double>> clsResult=new ArrayList<>();
        for (int i = 0; i<probs.size(); i++){
            clsResult.add(Arrays.stream(probs.get(i).split(",")).
                    map(s->Double.parseDouble(s)).collect(Collectors.toList()));
        }
        detectDTO.setClsProb(clsResult);

        return Result.success(detectDTO);
    }

    @GetMapping("/list/{userID}")
    public Result getList(@PathVariable Integer userID){
        QueryWrapper<History> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userID);
        return Result.success(historyService.list(queryWrapper));
    }

}
