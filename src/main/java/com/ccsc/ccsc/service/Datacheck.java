package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONObject;

public interface Datacheck {

//    校验通用的数据
    public Boolean checkdefault(JSONObject jsonObject);
//    实现特殊的校验
    public Boolean checkID(JSONObject jsonObject);
}
