package com.ccsc.ccsc.entry;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface Parsejson<T> {

    public T parsejsonwithInstance(JSONObject jsonObject) throws NoSuchAlgorithmException, NoSuchProviderException;
}
