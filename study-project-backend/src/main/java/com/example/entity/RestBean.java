package com.example.entity;

import lombok.Data;

@Data
public class RestBean<T> {
    private int status;
    private boolean success;
    private T message;

    private RestBean(int status, boolean success, T message){
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static  <T> RestBean<T> success() {
        return new RestBean<>(200,true,null);
    }

    public static  <T> RestBean<T> success(T data) {
        return new RestBean<>(200,true,data);
    }

    public static  <T> RestBean<T> failure(int status) {
        return new RestBean<>(status,false,null);
    }

    public static  <T> RestBean<T> failure(int status,T data) {
        return new RestBean<>(status,false,data);
    }

    /** 返回 HTTP status */
    public int getStatus() {
        return status;
    }

    /** 返回操作是否成功 */
    public boolean getSuccess() {
        return success;
    }

    /** 返回消息内容（泛型） */
    public T getMessage() {
        return message;
    }
}
