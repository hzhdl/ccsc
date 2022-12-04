package com.ccsc.ccsc.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Getter
@Setter
public class Result implements Serializable {

    private int code;
    private String msg;
    private Object data;
    private Object count;

    public static Result success(){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData("");
        return result;
    }
    public static Result success(Object data){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        return result;
    }
    public static Result success(Object data,Object count){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        return result;
    }
    public static Result failure(){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("fail");
        result.setData("");
        return result;
    }
    public static Result failure(Object data){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("fail");
        result.setData(data);
        return result;
    }
}
