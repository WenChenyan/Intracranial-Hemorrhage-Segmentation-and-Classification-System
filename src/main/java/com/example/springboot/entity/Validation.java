package com.example.springboot.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("validation")
@ApiModel(value = "Article对象", description = "")
public class Validation   {

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("邮箱")
    private String email ;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("验证类型")
    private Integer type;

    @ApiModelProperty("过期时间")
    private Date time;

}
