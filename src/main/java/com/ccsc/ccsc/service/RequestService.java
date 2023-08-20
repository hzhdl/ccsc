package com.ccsc.ccsc.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Invocation;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RequestService {

    @Resource
    ChainService chainService;
    private String body;


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
//    向指定的链分发消息。
    @Async
    public CompletableFuture<ResponseEntity<String>> requestUtil(Chain chain, Invocation invocation,String chainresult){

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
        jsonObject.put("Result",chainresult);
        jsonObject.put("ServerStime", invocation.getServerStime());
        jsonObject.put("ServerEtime",new Date().getTime());
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
//        forEntity.getBody();
        return CompletableFuture.completedFuture(forEntity);
    }

//    public ArrayList<JSONObject> activesync(JSONArray chainList, Invocation invocation){
//        ArrayList<JSONObject> arrayList=new ArrayList<>();
//        if (chainList==null||chainList.size()==0){
//            return new ArrayList<>();
//        }
//        else {
//            long stime = new Date().getTime();
//            for (int i = 0; i < chainList.size(); i++) {
//                Chain documentByChainHash = chainService.getDocumentByChainHash((String) chainList.getJSONObject(i).get("ChainHash"));
//                int finalI = i;
//                requestUtil(documentByChainHash, invocation, (String) invocation.getResult().getJSONObject(0).get("data")).thenApply((ResponseEntity<String> res) -> {
//                    String body = res.getBody();
//                    JSONObject jsonObject = JSONObject.parseObject(res.getBody());
//                    arrayList.add(jsonObject);
//                    return null;
//                });
//                System.out.println("第"+ finalI +"次循环结束,时间差为"+(new Date().getTime()-stime));
//            }
//            System.out.println("循环完成,时间差为"+(new Date().getTime()-stime));
//            return arrayList;
//        }
//    }

////    数据进行特别分发，不同于主动同步，不同的链有不同的查询结果数据
//    public ArrayList<JSONObject> halfactivesync(JSONArray multiqueryresult, Invocation invocation){
//        ArrayList<JSONObject> arrayList=new ArrayList<>();
//        if (multiqueryresult==null||multiqueryresult.size()==0){
//            return new ArrayList<>();
//        }
//        else {
//            for (int i = 0; i < multiqueryresult.size(); i++) {
//
//                Chain documentByChainHash = chainService.getDocumentByChainHash((String) multiqueryresult.getJSONObject(i).get("ChainHash"));
//                requestUtil(documentByChainHash, invocation, (String) multiqueryresult.getJSONObject(i).get("result")).thenApply((ResponseEntity<String> res) -> {
//                    String body = res.getBody();
//                    JSONObject jsonObject = JSONObject.parseObject(res.getBody());
//                    arrayList.add(jsonObject);
//                    return null;
//                });
//            }
//            return arrayList;
//        }
//    }

////    复杂查询，符合查询，查询数据。
//
//    public JSONObject multiquery(JSONArray chainList,Invocation invocation){
//        JSONObject jsonObject = new JSONObject();
//        AtomicReference<String> body= new AtomicReference<>("{}");
//        if (chainList==null||chainList.size()==0){
//            return new JSONObject();
//        }
//        else {
//            Chain documentByChainHash = chainService.getDocumentByChainHash(invocation.getChainHash());
//            requetmutilquery(documentByChainHash, invocation, chainList).thenApply((ResponseEntity<String> res)->{
////                System.out.println(res.getBody());
//                body.set(res.getBody());
//                return null;
//            });
//            return JSONObject.parseObject(body.get());
//        }
//    }
//    复杂查询请求
    @Async
    public CompletableFuture<ResponseEntity<String>> requetmutilquery(Chain chain, Invocation invocation,JSONArray schaindata){
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
        jsonObject.put("Chaindata",schaindata);
//        jsonObject.put("ServerStime", invocation.getServerStime());
//        jsonObject.put("ServerEtime",new Date().getTime());
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
        String url = chain.getAddress()+"/multiquery";
        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                url,
                httpEntity,
                String.class
        );//此处三个参数分别是请求地址、请求体以及返回参数类型
//        forEntity.getBody();
        return CompletableFuture.completedFuture(forEntity);
    }

//    被动数据更新
    //    复杂查询，符合查询，查询数据。

    public JSONObject passiveaccess(Chain chain, Invocation invocation, JSONObject schaindata){
        AtomicReference<String> body= new AtomicReference<>("{}");
        if (chain==null){
            return new JSONObject();
        }
        else {
            reqpassiveaccess(chain, invocation, schaindata).thenApply((ResponseEntity<String> res)->{
//                System.out.println(res.getBody());
                body.set(res.getBody());
                return null;
            });
            return JSONObject.parseObject(body.get());
        }
    }
//    异步数据访问
    @Async
    public CompletableFuture<ResponseEntity<String>> reqpassiveaccess(Chain chain, Invocation invocation, JSONObject schaindata){
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
        jsonObject.put("Chaindata",schaindata);
        jsonObject.put("ServerStime", invocation.getServerStime());
        jsonObject.put("ServerEtime",new Date().getTime());
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
        String url = chain.getAddress()+"/passiveaccessget";
        ResponseEntity<String> forEntity = restTemplate.postForEntity(
                url,
                httpEntity,
                String.class
        );//此处三个参数分别是请求地址、请求体以及返回参数类型
//        forEntity.getBody();
        return CompletableFuture.completedFuture(forEntity);
    }
}
