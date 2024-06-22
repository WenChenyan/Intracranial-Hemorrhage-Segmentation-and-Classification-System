package com.example.springboot.common;



public enum ValidationEnum {
    //自定义的状态码

    LOGIN(303), FORGET_PASS(304),AUTHENTICATE(305);

    //错误码

    public Integer code;


    ValidationEnum(Integer code) {
        this.code = code ;
    }

    //获取状态码

    public Integer getCode() {

        return code;

    }

}
