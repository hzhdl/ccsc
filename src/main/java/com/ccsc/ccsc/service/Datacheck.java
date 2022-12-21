package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.util.RSACipher;

public interface Datacheck {

//    校验通用的数据
    public Boolean checkdefault(JSONObject jsonObject);
//    实现特殊的校验
    public Boolean checkID(JSONObject jsonObject);

    public default Boolean checksignature(JSONObject jsonObject){
        String s = jsonObject.getJSONObject("data").getString("Publickey");
        String data1 = jsonObject.getString("data");
        String msignature = jsonObject.getString("Msignature");
//        String prvkey = jsonObject.getString("Csignature");
//        String sign = RSACipher.sign(
//                prvkey,
//                data1.getBytes()
//        );
        return RSACipher.checkSign(
                s,
                data1.getBytes(),
                msignature
        );
    }
    public default Boolean checksignature(JSONObject jsonObject, String publickey){
//        String s = jsonObject.getJSONObject("data").getString("Publickey");
        String data1 = jsonObject.getString("data");
        String msignature = jsonObject.getString("Csignature");
//        String prvkey = jsonObject.getString("Csignature");
//        String sign = RSACipher.sign(
//                prvkey,
//                data1.getBytes()
//        );
        return RSACipher.checkSign(
                publickey,
                data1.getBytes(),
                msignature
        );
    }
    public default Boolean checkMsignature(JSONObject jsonObject, String publickey){
//        String s = jsonObject.getJSONObject("data").getString("Publickey");
        String data1 = jsonObject.getString("data");
        String msignature = jsonObject.getString("Msignature");
//        String prvkey = jsonObject.getString("Csignature");
//        String sign = RSACipher.sign(
//                prvkey,
//                data1.getBytes()
//        );
        return RSACipher.checkSign(
                publickey,
                data1.getBytes(),
                msignature
        );
    }
}
