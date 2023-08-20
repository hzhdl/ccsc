package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.*;
import com.ccsc.ccsc.service.*;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/Invocation")
@CrossOrigin
@Api(tags = "Invocation", description = "Invocation控制层,负责实际的状态更新等操作")
public class Invocationcontroller {

    @Resource
    ChainService chainService;

    @Resource
    ContractService contractService;
    @Resource
    SubscribeService subscribeService;
    @Resource
    InvocationService invocationService;

    @Resource
    RequestService requestService;




/*    {
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
    @RequestMapping("/activesync")
    @ApiOperation("主动更新")
    public Result activesync(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
        System.out.println("主动更新访问");
        JSONObject jsonObject1=JSONObject.parseObject(data);
        long ServerStime = new Date().getTime();
//        首先校验Msignature
//        校验签名
        String s  = (String) jsonObject1.getJSONObject("data").get("CCSCHash");
        Contract cc=contractService.getDocumentByCCSCHash(s);
        Boolean checksignature = contractService.checkMsignature(jsonObject1,cc.getServerPublicKey());
        if (!checksignature){
            return Result.failure("M签名校验失败");
        }
//        校验Csignature，注意要将data和Msiganture直接拼接以后进行签名校验。
        String s1  = (String) jsonObject1.getJSONObject("data").get("ChainHash");
        Chain cc1=chainService.getDocumentByChainHash(s1);
        Boolean checksignature1 = chainService.checksignature(jsonObject1,cc1.getServerPublicKey());
        if (!checksignature1){
            return Result.failure("C签名校验失败");
        }
//        构建Invocation实例
        Parsejson<Invocation> parsejson = new Invocation();
        Invocation invocation = parsejson.parsejsonwithInstance(jsonObject1);
        invocation.setServerStime(ServerStime);
//        通知各个目标链进行更新,主动更新
//        首先取出订阅的ChainHash列表
        Subscribe subscribe = subscribeService.getDocumentByCCSCHash(invocation.getCCSCHash(), invocation.getChainHash());

//        取出订阅的Chain列表
        JSONArray sChainHash = subscribe.getSChainHash();

//        通知订阅链上的状态更新
        ArrayList<JSONObject> activesync = activesync(sChainHash, invocation);

        long time1 = new Date().getTime();
//        System.out.println(activesync.toString());
        JSONArray timellist = new JSONArray();
        var ref = new Object() {
            Long max = 0L;
        };
        AtomicReference<Double> totalaverage= new AtomicReference<>(0.0);
        activesync.forEach((jsonObject -> {
            JSONObject time = new JSONObject();
            time.put("TchainStime",invocation.getInput().getLong("TchainStime"));
            time.put("TchainEtime",invocation.getInput().getLong("TchainEtime"));
            time.put("ServerStime",invocation.getServerStime());
//            System.out.println(jsonObject);
            time.put("ServerEtime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("ServerEtime"));
            time.put("SchainStime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("SchainStime"));
            time.put("SchainEtime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("SchainEtime"));
            time.put("Setime",time.getLong("ServerEtime")-time.getLong("ServerStime"));
            time.put("Stime",time.getLong("SchainEtime")-time.getLong("SchainStime"));
            time.put("Ttime",time.getLong("TchainEtime")-time.getLong("TchainStime"));
            time.put("Totaltime",time.getLong("SchainEtime")-time.getLong("TchainStime"));
            totalaverage.updateAndGet(v -> v + time.getLong("Totaltime"));
            ref.max =time.getLong("Totaltime")> ref.max ?time.getLong("Totaltime"):ref.max ;
            timellist.add(time);
        }));
        for (int i = 0; i < timellist.size(); i++) {
            timellist.getJSONObject(i).put("Averagetime",ref.max/activesync.size());
            timellist.getJSONObject(i).put("Maxtime",ref.max);
            timellist.getJSONObject(i).put("Signletime", totalaverage.get() /activesync.size());
            timellist.getJSONObject(i).put("Averages",activesync.size());
        }
        invocation.setTime(timellist);

//        根据结果将向数据库插入数据
        invocation.setResponselist(activesync);

        boolean b = invocationService.saveInvocation(invocation);

        if (b){
            return Result.success("同步成功");
        }else {
            return Result.failure("同步失败");
        }


    }

    @RequestMapping("/halfactivesync")
    @ApiOperation("半主动更新")
    public Result halfactive(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
//        System.out.println(data);
        JSONObject jsonObject1=JSONObject.parseObject(data);
        long ServerStime = new Date().getTime();
//        首先校验Msignature
//        校验签名
        String s  = (String) jsonObject1.getJSONObject("data").get("CCSCHash");
        Contract cc=contractService.getDocumentByCCSCHash(s);
        Boolean checksignature = contractService.checkMsignature(jsonObject1,cc.getServerPublicKey());
        if (!checksignature){
            return Result.failure("M签名校验失败");
        }
//        校验Csignature，注意要将data和Msiganture直接拼接以后进行签名校验。
        String s1  = (String) jsonObject1.getJSONObject("data").get("ChainHash");
        Chain cc1=chainService.getDocumentByChainHash(s1);
        Boolean checksignature1 = chainService.checksignature(jsonObject1,cc1.getServerPublicKey());
        if (!checksignature1){
            return Result.failure("C签名校验失败");
        }
//        构建Invocation实例
        Parsejson<Invocation> parsejson = new Invocation();
        Invocation invocation = parsejson.parsejsonwithInstance(jsonObject1);
        invocation.setServerStime(ServerStime);
//        通知各个目标链进行更新,主动更新
//        首先取出订阅的ChainHash列表
        Subscribe subscribe = subscribeService.getDocumentByCCSCHash(invocation.getCCSCHash(), invocation.getChainHash());

//        取出订阅的Chain列表
        JSONArray sChainHash = subscribe.getSChainHash();
//        请求复杂查询
        JSONObject multiquerys = multiquery(sChainHash,invocation);
        System.out.println("***************"+(new Date().getTime() - ServerStime));
        JSONArray resultjson = multiquerys.getJSONObject("data").getJSONArray("result");
        System.out.println(resultjson.toString());

//        分发数据
        ArrayList<JSONObject> halfactivesync = halfactivesync(resultjson, invocation);

        long time1 = new Date().getTime();
        JSONArray timellist = new JSONArray();
        var ref = new Object() {
            Long max = 0L;
        };
        AtomicReference<Double> totalaverage= new AtomicReference<>(0.0);
        halfactivesync.forEach((jsonObject -> {
            JSONObject time = new JSONObject();

            time.put("TchainStime",invocation.getInput().getLong("TchainStime"));
            time.put("TchainEtime",invocation.getInput().getLong("TchainEtime"));
            time.put("ServerStime",invocation.getServerStime());
            time.put("ServerEtime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("ServerEtime"));
            time.put("SchainStime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("SchainStime"));
            time.put("SchainEtime",jsonObject.getJSONObject("data").getJSONObject("data").getLong("SchainEtime"));
            time.put("Setime",time.getLong("ServerEtime")-time.getLong("ServerStime"));
            time.put("Stime",time.getLong("SchainEtime")-time.getLong("SchainStime"));
            time.put("Ttime",time.getLong("TchainEtime")-time.getLong("TchainStime"));
            time.put("Totaltime",time.getLong("SchainEtime")-time.getLong("TchainStime"));
            totalaverage.updateAndGet(v -> v + time.getLong("Totaltime"));
            ref.max =time.getLong("Totaltime")> ref.max ?time.getLong("Totaltime"):ref.max ;
            timellist.add(time);
        }));
        for (int i = 0; i < timellist.size(); i++) {
            timellist.getJSONObject(i).put("Averagetime",ref.max/halfactivesync.size());
            timellist.getJSONObject(i).put("Maxtime",ref.max);
            timellist.getJSONObject(i).put("Signletime", totalaverage.get() / halfactivesync.size());
            timellist.getJSONObject(i).put("Averages",halfactivesync.size());
        }
        invocation.setTime(timellist);

        invocation.setResult(resultjson);
        invocation.setResponselist(halfactivesync);
        boolean b = invocationService.saveInvocation(invocation);

        if (b){
            return Result.success("同步成功");
        }else {
            return Result.failure("同步失败");
        }
//        准备要返回的数据
//        JSONObject resultdata= new JSONObject();
//        resultdata.put("ChainHash",contract1.getChainHash());
//        resultdata.put("CCSChash",contract1.getCCSCHash());
//        resultdata.put("Flag",contract1.getFlag());
////            合约的接口通信均采用链注册的公私钥，合约注册不需要再次生成新的公私钥，仅记录合约接口的公钥即可。
//        resultdata.put("Publickey",cc.getClientPublicKey());
////            System.out.println(resultdata.toString());
//        Result result= Result.success();
//        result.setMsg("注册成功")
//                .setCsignature(
//                        RSACipher.sign(
//                                cc.getClientSerectKey(),
//                                resultdata.toString().getBytes()
//                        )
//                )
//                .setMsignature(
//                        RSACipher.sign(
//                                contract1.getClientSerectKey(),
//                                resultdata.toString().getBytes()
//                        )
//                )
//                .setEncryptflag(false)
//                .setData(resultdata.toString());
//            System.out.println(cc.getClientSerectKey());
    }

    @RequestMapping("/passiveaccess")
    @ApiOperation("被动访问")
    public Result passiveaccess(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
//        System.out.println(data);
        JSONObject jsonObject1=JSONObject.parseObject(data);
        long ServerStime = new Date().getTime();
//        首先校验Msignature
//        校验签名
//        String s  = (String) jsonObject1.getJSONObject("data").get("CCSCHash");
//        Contract cc=contractService.getDocumentByCCSCHash(s);
//        Boolean checksignature = contractService.checkMsignature(jsonObject1,cc.getServerPublicKey());
//        if (!checksignature){
//            return Result.failure("M签名校验失败");
//        }
//        校验Csignature，注意要将data和Msiganture直接拼接以后进行签名校验。
        String s1  = (String) jsonObject1.getJSONObject("data").get("SrcChainHash");
        Chain cc1=chainService.getDocumentByChainHash(s1);
        Boolean checksignature1 = chainService.checksignature(jsonObject1,cc1.getServerPublicKey());
        if (!checksignature1){
            return Result.failure("C签名校验失败");
        }
//        构建Invocation实例
        Parsejson<Invocation> parsejson = new Invocation();
        Invocation invocation = parsejson.parsejsonwithInstance(jsonObject1);
        invocation.setServerStime(ServerStime);
        invocation.setSrcChainHash(s1);
//        Flag矫正，纠正人为导致的Flag错误
        invocation.setFlag("2");
//        通知目标链，获取数据或者更新
        Chain documentByChainHash = chainService.getDocumentByChainHash(invocation.getChainHash());
//        请求数据查询
        JSONObject argjson = new JSONObject();
        argjson.put("data",jsonObject1.getJSONObject("data").get(""));

        JSONObject passiveaccess = requestService.passiveaccess(documentByChainHash, invocation, argjson);

        Long lastime=new Date().getTime();
        System.out.println(passiveaccess.getJSONObject("data"));
//      生成相关invocation数据,
//        配置invocation的被动数据
        invocation.setPassiveResult(passiveaccess.getJSONObject("data").getJSONObject("result"));
        //        计算前半段的时间
        Long pretime=passiveaccess.getJSONObject("data").getLong("ServerEtime")-invocation.getServerStime();
        ArrayList<JSONObject> response = new ArrayList<>();
        response.add(passiveaccess);
        invocation.setResponselist(response);


        JSONArray timellist = new JSONArray();
        JSONObject time = new JSONObject();
        time.put("SchainStime",invocation.getInput().getLong("SchainStime"));
        time.put("SchainEtime",invocation.getInput().getLong("SchainEtime"));
        time.put("ServerStime",invocation.getServerStime());
//        time.put("ServerPretime",pretime);
        time.put("ServerEtime",new Date().getTime());
        time.put("TchainStime",passiveaccess.getJSONObject("data").getLong("TchainStime"));
        time.put("TchainEtime",passiveaccess.getJSONObject("data").getLong("TchainEtime"));
        time.put("Setime",pretime+new Date().getTime()- lastime);
        time.put("Stime",time.getLong("SchainEtime")-time.getLong("SchainStime"));
//        time.put("Ttime",time.getLong("TchainEtime")-time.getLong("TchainStime"));
        time.put("Ttime",lastime-passiveaccess.getJSONObject("data").getLong("ServerEtime"));
        time.put("Totaltime",time.getLong("Setime")+time.getLong("Stime"));
        timellist.add(time);

        invocation.setTime(timellist);
        boolean b = invocationService.saveInvocation(invocation);

        if (b){
//        准备要返回的数据

        JSONObject resultdata= new JSONObject();
        resultdata.put("ChainHash",invocation.getChainHash());
        resultdata.put("CCSChash",invocation.getCCSCHash());
        resultdata.put("SrcChainHash",invocation.getSrcChainHash());
        resultdata.put("Flag",invocation.getFlag());
        resultdata.put("Result",invocation.getPassiveResult());
        resultdata.put("Tchainresponse",passiveaccess);
        resultdata.put("Time",time);

            Chain cc = chainService.getDocumentByChainHash(invocation.getSrcChainHash());
            Result result= Result.success();
        result.setMsg("注册成功")
                .setCsignature(
                        RSACipher.sign(
                                cc.getClientSerectKey(),
                                resultdata.toString().getBytes()
                        )
                )
                .setEncryptflag(false)
                .setData(resultdata.toString());
//            System.out.println(cc.getClientSerectKey());
            return result;
        }else {
            return Result.failure("同步失败");
        }

//        return Result.failure("已接收参数");
    }





    /*
    * 以下为迁移的多线程代码
    * */
    public ArrayList<JSONObject> activesync(JSONArray chainList, Invocation invocation){
        ArrayList<JSONObject> arrayList=new ArrayList<>();
        if (chainList==null||chainList.size()==0){
            return new ArrayList<>();
        }
        else {
            List<CompletableFuture<ResponseEntity<String>>> completableFutureList=new ArrayList<CompletableFuture<ResponseEntity<String>>>();
            for (int i = 0; i < chainList.size(); i++) {
                Chain documentByChainHash = chainService.getDocumentByChainHash((String) chainList.getJSONObject(i).get("ChainHash"));
                int finalI = i;
                System.out.println("--------"+invocation.getResult().getJSONObject(0).toJSONString());
                completableFutureList.add(requestService.requestUtil(documentByChainHash, invocation, invocation.getResult().getJSONObject(0).toJSONString()).thenApply((ResponseEntity<String> res) -> {
                    String body = res.getBody();
                    JSONObject jsonObject = JSONObject.parseObject(res.getBody());
                    arrayList.add(jsonObject);
                    return null;
                }));
//                System.out.println("第"+ finalI +"次循环结束,时间差为"+(new Date().getTime()-stime));
            }
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
//            System.out.println("循环完成,时间差为"+(new Date().getTime()-stime));
            return arrayList;
        }
    }

    //    数据进行特别分发，不同于主动同步，不同的链有不同的查询结果数据
    public ArrayList<JSONObject> halfactivesync(JSONArray multiqueryresult, Invocation invocation){
        ArrayList<JSONObject> arrayList=new ArrayList<>();
        if (multiqueryresult==null||multiqueryresult.size()==0){
            return new ArrayList<>();
        }
        else {
            List<CompletableFuture<ResponseEntity<String>>> completableFutureList=new ArrayList<CompletableFuture<ResponseEntity<String>>>();

            for (int i = 0; i < multiqueryresult.size(); i++) {

                Chain documentByChainHash = chainService.getDocumentByChainHash((String) multiqueryresult.getJSONObject(i).get("ChainHash"));
                completableFutureList.add(requestService.requestUtil(documentByChainHash, invocation, multiqueryresult.getJSONObject(i).toJSONString()).thenApply((ResponseEntity<String> res) -> {
                    String body = res.getBody();
                    JSONObject jsonObject = JSONObject.parseObject(res.getBody());
                    arrayList.add(jsonObject);
                    return null;
                }));
            }
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
            return arrayList;
        }
    }

    //    复杂查询，符合查询，查询数据。

    public JSONObject multiquery(JSONArray chainList,Invocation invocation){
        JSONObject jsonObject = new JSONObject();
        AtomicReference<String> body= new AtomicReference<>("{}");
        if (chainList==null||chainList.size()==0){
            return new JSONObject();
        }
        else {
            Chain documentByChainHash = chainService.getDocumentByChainHash(invocation.getChainHash());

            CompletableFuture.allOf(requestService.requetmutilquery(documentByChainHash, invocation, chainList).thenApply((ResponseEntity<String> res)->{
//                System.out.println(res.getBody());
                body.set(res.getBody());
                return null;
            })).join();
            return JSONObject.parseObject(body.get());
        }
    }

}
