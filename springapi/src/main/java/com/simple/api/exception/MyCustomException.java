package com.simple.api.exception;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MyCustomException extends RuntimeException{

    private int code;
    private String msg;
    private Exception e;

}
