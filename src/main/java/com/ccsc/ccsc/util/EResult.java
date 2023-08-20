package com.ccsc.ccsc.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@Accessors(chain = true)
//用来获取评估后的数据信息
public class EResult {
    private int code;
    private String msg;
    private Object data;
    private Object count;
    private Object exdata;

    public static EResult success(Object data){
        return new EResult()
                .setCode(0)
                .setMsg("success")
                .setData(data)
                .setCount(0)
                .setExdata("");

    }
    public static EResult success(String msg,Object data){
        return new EResult()
                .setCode(0)
                .setMsg(msg)
                .setData(data)
                .setCount(0)
                .setExdata("");
    }
    public static EResult success(String msg,Object data,Object count){
        return new EResult()
                .setCode(0)
                .setMsg(msg)
                .setData(data)
                .setCount(count)
                .setExdata("");
    }
    public static EResult success(String msg,Object data,Object count,Object exdata){
        return new EResult()
                .setCode(0)
                .setMsg(msg)
                .setData(data)
                .setCount(count)
                .setExdata(exdata);
    }

    public static EResult success(String msg){
        return new EResult()
                .setCode(1)
                .setMsg("false")
                .setData("")
                .setCount(0)
                .setExdata("");
    }

}
