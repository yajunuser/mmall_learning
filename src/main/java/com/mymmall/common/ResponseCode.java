package com.mymmall.common;

/**
 * 枚举类，需要写他的构造器
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    //错误失败
    ERROR(1, "ERROR"),
    //需要强制登录
    NEED_LOGIN(10,"NEED_LOGIN"),
    //参数错误
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;
//枚举的构造器  两个参数
    ResponseCode(int code,String desc){
        this.code=code;
        this.desc = desc;
    }
    public int getCode(){
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
