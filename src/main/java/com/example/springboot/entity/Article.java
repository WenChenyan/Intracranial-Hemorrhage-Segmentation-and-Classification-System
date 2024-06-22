package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@ApiModel(value = "Article对象", description = "")
public class Article implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  @ApiModelProperty("标题")
  private String title ;

  @ApiModelProperty("内容")
  private String content;

  @ApiModelProperty("发布人昵称")
  private String nickname;

  @ApiModelProperty("发布人用户名")
  private String user;

  @ApiModelProperty("发布时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
  private Date time;


  @ApiModelProperty("类型")
  private String type;

  @ApiModelProperty("图片地址")
  private String img;

  @ApiModelProperty("论文地址")
  private String address;

  @TableField(exist = false)
  private Integer rank ;

  @TableField(exist = false)

  private Integer countComment ;

}
