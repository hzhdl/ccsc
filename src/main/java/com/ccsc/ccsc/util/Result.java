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
    private Object exdata;
    private String Msignature;
    private String Csignature;
    private Boolean encryptflag;

    public static Result success(){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData("");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        return result;
    }
    public static Result success(Object data){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        return result;
    }
    public static Result success(Object data,Object count,Object exdata){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        return result;
    }
    public static Result success(Object data,Object count,String exdata, String msignature, String csignature,Boolean encryptflag){
        Result result=new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(data);
        result.setCount(count);
        result.setExdata("");
        result.setMsignature(msignature);
        result.setCsignature(csignature);
        result.setEncryptflag(encryptflag);
        return result;
    }
    public static Result failure(){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("fail");
        result.setData("");
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
        return result;
    }
    public static Result failure(Object data){
        Result result=new Result();
        result.setCode(0);
        result.setMsg("fail");
        result.setData(data);
        result.setExdata("");
        result.setMsignature("");
        result.setCsignature("");
        result.setEncryptflag(false);
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
        return result;
    }
}
