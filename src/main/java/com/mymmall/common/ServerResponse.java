package com.mymmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 高复用的服务返回对象，就是controller层的返回方便快捷
 *用泛型创造的服务端响应对象！
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候，如果结果是null的对象，key也会消失；
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    //创建私有构造器 并提供工共的访问方法
    private ServerResponse(int status) {
        this.status = status;
    }
    private ServerResponse(int status, String msg) {
        this.msg = msg;
        this.status = status;
    }
    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }
    //判断返回信息是不是成功,通过枚举常量来判断 错误信息
    @JsonIgnore
    //使之不在json序列化结果当中
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus() {
        return status;
    }
    public T getData() {
        return data;
    }
    public String getMsg(){
        return msg;
    }
//继续创建开放工共方法
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }
    public  static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }
    //创建错误的提示信息
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }
    public  static <T> ServerResponse<T> createByErrorMessage(String errormsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errormsg);
    }
    public static <T> ServerResponse<T> createByCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<T>(errorCode, errorMessage);
    }
}
