package com.ccsc.ccsc.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Invocation;
import com.ccsc.ccsc.entry.Subscribe;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {

    @Resource
    ChainService chainService;


    /*{
            code: 1,
            msg: 'success',
            data: {
                ChainHash:"c7953e0a44a812d007ac3e438ca8c77bd8ad6d2c7ac3f47be33dadee37f32da66d83c9202b17d000ac2e1b5ce1b1c5890e0768189750b0b25b59d93c3a835f6c",
                CCSCHash:"1444801be194caa9de88c4eefc7fe0de5b5328b01072493bc769402029d3d50b317a94a160faa4ef5be173497951ffadaf7ea5f4d4890e04e76002a7fbc96f14",
                Result:{},
                Flag:"0",
                Status:"0",
                Exdata:"0"
    },
        count: '',
                exdata: '',
            encryptflag: false,
            Msignature:'',
            Csignature: ''
    }*/
    public String requestUtil(Chain chain,Invocation invocation){

//        String adress = chain.getAddress();
//        构建请求的http
        RestTemplate restTemplate = new RestTemplate();
        //创建请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //此处相当于在Authorization里头添加Bear token参数信息
//        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + assessToken);
        //此处相当于在header里头添加content-type等参数
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,"application/json");
//        Map<String, Object> map = getMap(documentId);
//        String jsonStr = JSONObject.toJSONString(jsonObject);
//        构造标准的请求接口数据
        Result result = Result.success();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("input",invocation.getInput());
        jsonObject.put("Msignature",invocation.getMsignature());
        jsonObject.put("chain", chain.toJSONString());
        result.setData(jsonObject.toString())
                .setMsignature("")/*添加M签名数据*/
                .setCsignature(
                    RSACipher.sign(
                        chain.getClientSerectKey(),
                        jsonObject.toString().getBytes()
                    )
                );/*添加C签名数据*/

        Map<String,Object> map = Result.resultTomap(result);
        //创建请求体并添加数据
        HttpEntity<Map> httpEntity = new HttpEntity<>(map, httpHeaders);
//        根据chain获取网关URL，并进行访问
        String url = chain.getAddress()+"/activesync";
        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                url,
                httpEntity,
                String.class
        );//此处三个参数分别是请求地址、请求体以及返回参数类型

        return forEntity.toString();
    }

    public ArrayList<String> activesync(List<String> chainList, Invocation invocation){
        ArrayList<String> arrayList=new ArrayList<>();

        if (chainList==null||chainList.size()==0){
            return new ArrayList<>();
        }
        else {
            for (String CHash : chainList) {
                Chain documentByChainHash = chainService.getDocumentByChainHash(CHash);
                arrayList.add(requestUtil(documentByChainHash,invocation));
            }
            return arrayList;
        }
    }
}
