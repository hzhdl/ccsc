package com.ccsc.ccsc.util;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@Setter
@Accessors(chain = true)
public class Result implements Serializable {

    private int code;
    private String msg;
    private Object data;
    private Object count;
    private Object exdata;
    private String Msignature;
    private String Csignature;
    private Boolean encryptflag;

    public static Result success(){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData("");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result success(Object data){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result success(String msg){
        Result result=new Result();
        result.setCode(0);
        result.setMsg(msg);
        result.setData("");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result success(Object data,Object count,Object exdata){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result success(Object data,Object count,String exdata, String msignature, String csignature,Boolean encryptflag){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature(msignature);
        result.setCsignature(csignature);
        result.setEncryptflag(encryptflag);
        result.setCount("");
        return result;
    }


    public static Result failure(){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("fail");
        result.setData("");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result failure(Object data){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("fail");
        result.setData(data);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result failure(String msg){
        Result result=new Result();
        result.setCode(1);
        result.setMsg(msg);
        result.setData("data");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result failure(Object data,Object count,Object exdata){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("fail");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        result.setCount("");
        return result;
    }
    public static Result failure(Object data,Object count,String exdata, String msignature, String csignature,Boolean encryptflag){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("fail");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature(msignature);
        result.setCsignature(csignature);
        result.setEncryptflag(encryptflag);
        result.setCount("");
        return result;
    }

    public static Map<String,Object> resultTomap(Result result){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code",result.code);
        map.put("msg",result.msg);
        map.put("data",result.data);
        map.put("count",result.count);
        map.put("exdata",result.exdata);
        map.put("Msignature",result.Msignature);
        map.put("Csignature",result.Csignature);
        map.put("encryptflag",result.encryptflag);
        return map;
    }
}
