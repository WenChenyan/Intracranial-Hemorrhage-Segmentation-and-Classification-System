package com.example.springboot.controller.DeepLearningModelPredict;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.DeepLearningModelPredict.dto.DetectDTO;
import com.example.springboot.controller.DeepLearningModelPredict.request.DetectRequest;
import com.example.springboot.entity.History;
import com.example.springboot.service.IHistoryService;
import com.example.springboot.service.IImageService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin //解决跨域问题
@RestController
@RequestMapping("/detectImage")
public class DetectImageController {

    @Value("${server.ip}")
    private String serverIp;

    @Autowired
    private IImageService imageConsultService;

    @Autowired
    private IHistoryService historyService;


    @PostMapping("/OutWithCache")
    public Result OutWithCache(@RequestBody DetectRequest detectRequest) throws Exception {

        // step 1. detect image
        String detectOut = imageConsultService.detectInQueue(detectRequest);

        List<String> Info=Arrays.stream(detectOut.split(";")).collect(Collectors.toList());

        DetectDTO detectDTO = new DetectDTO();
        History history = new History();
        history.setUserId(Objects.requireNonNull(TokenUtils.getCurrentUser()).getId());
        history.setTime(DateUtil.now());
        history.setSegModel(detectRequest.getSegModelName());
        history.setClsModel(detectRequest.getClsModelName());
        history.setPatientName(detectRequest.getPatientName());
        history.setPatientSex(detectRequest.getPatientSex());
        history.setPatientInfo(detectRequest.getPatientInfo());

        StringBuilder filesUrl = new StringBuilder();
        for(int i=0;i<detectRequest.getFilesUrl().size();i+=1){
            filesUrl.append(detectRequest.getFilesUrl().get(i));
            if(i<detectRequest.getFilesUrl().size()-1)
                filesUrl.append("#");
        }

        history.setInputFiles("075.nii");

        if (Info.get(0).equals("3D")){

            history.setExistModel(true);
            history.setModelUrl(Info.get(1));
            history.setSegUrl(Info.get(2));
            history.setClsProb(Info.get(3));

            List<String> modelUrl=Arrays.stream(Info.get(1).split("#")).collect(Collectors.toList());
            List<String> segImageUrl=Arrays.stream(Info.get(2).split("#")).collect(Collectors.toList());
            List<String> probs=Arrays.stream(Info.get(3).split("#")).collect(Collectors.toList());
            List<List<Double>> clsResult=new ArrayList<>();
            for (int i = 0; i<probs.size(); i++){
                clsResult.add(Arrays.stream(probs.get(i).split(",")).
                        map(s->Double.parseDouble(s)).collect(Collectors.toList()));
            }
            detectDTO.setExistModel(true);
            detectDTO.setModelUrl(modelUrl);
            detectDTO.setSegUrl(segImageUrl);
            detectDTO.setClsProb(clsResult);
        }
        else {

            history.setExistModel(false);
            history.setSegUrl(Info.get(1));
            history.setClsProb(Info.get(2));

            List<String> segImageUrl=Arrays.stream(Info.get(1).split("#")).collect(Collectors.toList());
            List<String> probs=Arrays.stream(Info.get(2).split("#")).collect(Collectors.toList());
            List<List<Double>> clsResult=new ArrayList<>();
            for (int i = 0; i<probs.size(); i++){
                clsResult.add(Arrays.stream(probs.get(i).split(",")).
                        map(s->Double.parseDouble(s)).collect(Collectors.toList()));
            }
            detectDTO.setExistModel(false);
            detectDTO.setSegUrl(segImageUrl);
            detectDTO.setClsProb(clsResult);
        }
        historyService.save(history);
        // step 3. return seg result page
        return Result.success(detectDTO);
    }

}
