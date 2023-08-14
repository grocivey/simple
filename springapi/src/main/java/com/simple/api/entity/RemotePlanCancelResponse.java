package com.simple.api.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RemotePlanCancelResponse {
    //状态码
    private Integer code;
    //请求描述信息
    private String msg;
    //返回数据
    private Object data;


}
