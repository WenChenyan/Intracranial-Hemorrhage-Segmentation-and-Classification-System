package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@TableName("history")
@ApiModel(value = "History对象", description = "")
public class History {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("userID")
    private Integer userId;
    @ApiModelProperty("封面")
    private String segImg;
    private String clsImg;
    @ApiModelProperty("输入文件")
    private String inputFiles;
    @ApiModelProperty("存在3D模型")
    private Boolean existModel;
    @ApiModelProperty("3D模型")
    private String modelUrl;
    @ApiModelProperty("img分割结果")
    private String segUrl;
    @ApiModelProperty("分类结果")
    private String clsProb ;
    @ApiModelProperty("分割模型")
    private String segModel;
    @ApiModelProperty("分类模型")
    private String clsModel;
    @ApiModelProperty("病人姓名")
    private String patientName;
    @ApiModelProperty("病人性别")
    private String patientSex;
    @ApiModelProperty("病史")
    private String patientInfo;
    @ApiModelProperty("检测时间")
    private String time;

}

