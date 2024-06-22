package com.example.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.springboot.common.ServiceConstants;
import com.example.springboot.controller.DeepLearningModelPredict.request.DetectRequest;
import com.example.springboot.service.IImageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class ImageService implements IImageService {

    private Logger logger= LoggerFactory.getLogger(ImageService.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String IMAGE_QUEUE = "imageQueue";

    private static final String CONSULT_OUT = "consultOut";

    private static final String DETECT_KEY = "detectKey";
    private static final String EXIST_NII = "existNii";
    private static final String FILES_URL = "filesUrl";
    private static final String CLS_MODEL_NAME = "ClsModelName";
    private static final String SEG_MODEL_NAME = "SegModelName";

    private static final Set<Long> TIME_INTERVALS = new HashSet<Long>() {
        {
            this.add(500L);
            this.add(1000L);
            this.add(2000L);
            this.add(4000L);
            this.add(8000L);
            this.add(12000L);
            this.add(16000L);
        }
    };

    @Override
    public String detectInQueue(DetectRequest detectRequest) throws Exception {
        //step 1. Is the queue overrun
        if (stringRedisTemplate.opsForList().size(IMAGE_QUEUE) > ServiceConstants.THREAD_STACK_SIZE) {
            logger.error("thread stack size is out of limit");
            throw new Exception("System busy,Please try again later");
        }

        //step 2. Put imageUrl in queue
        Map<String, Object> imageInfo = new HashMap<>();
        String detectKey =  UUID.randomUUID().toString(); // (IdUtil.fastSimpleUUID() + StrUtil.DOT);

        StringBuilder filesUrl = new StringBuilder();
        for(int i=0;i<detectRequest.getFilesUrl().size();i+=1){
            filesUrl.append(detectRequest.getFilesUrl().get(i));
            if(i<detectRequest.getFilesUrl().size()-1)
                filesUrl.append("#");
        }

        imageInfo.put(DETECT_KEY, detectKey);
        imageInfo.put(EXIST_NII, detectRequest.getExistNii());
        imageInfo.put(FILES_URL, filesUrl.toString());
        imageInfo.put(CLS_MODEL_NAME, detectRequest.getClsModelName() );
        imageInfo.put(SEG_MODEL_NAME, detectRequest.getSegModelName() );
        stringRedisTemplate.opsForList().leftPush(IMAGE_QUEUE, JSON.toJSONString(imageInfo));

        //step 3. Get the result
        for (Long interval : TIME_INTERVALS) {
            try {
                Thread.currentThread().sleep(interval.longValue());
                Object consultResult = stringRedisTemplate.opsForHash().get(detectKey, CONSULT_OUT);
                if (consultResult != null) {
                    //delete result in cache
                    stringRedisTemplate.delete(detectKey);
                    return (String) consultResult;
                }
            } catch (InterruptedException e) {
                logger.error("Seg service thread error");
            }
        }
        return null;
    }

}
