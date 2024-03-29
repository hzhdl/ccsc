package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.entry.Parsejson;
import com.ccsc.ccsc.entry.Subscribe;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.service.ContractService;
import com.ccsc.ccsc.service.Datacheck;
import com.ccsc.ccsc.service.SubscribeService;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Contract")
@CrossOrigin
@Api(tags = "Contract", description = "Contract控制层")
public class contractcontroller {

    @Resource
    ContractService contractService;

    @Resource
    ChainService chainService;

    @Resource
    SubscribeService subscribeService;

    @RequestMapping("/register")
    @ApiOperation("注册")
    public Result register(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
        System.out.println("Constract register:"+data);
        JSONObject jsonObject1=JSONObject.parseObject(data);
//        检验数据正确性
        Datacheck datacheck=contractService;
        Boolean checkdefault = datacheck.checkdefault(jsonObject1);

        if (checkdefault){
            //        校验签名
            String s  = (String) jsonObject1.getJSONObject("data").get("ChainHash");
            Chain cc=chainService.getDocumentByChainHash(s);
            Boolean checksignature = chainService.checksignature(jsonObject1,cc.getServerPublicKey());
            if (!checksignature){
                return Result.failure("签名校验失败");
            }

            Parsejson<Contract> parsejson=new Contract();
            Contract contract = parsejson.parsejsonwithInstance(jsonObject1);
            if (contract == null){
                return Result.failure("服务器错误，请稍后重试！");
            }
//            Chain chainHash = chainService.getDocumentByChainHash(contract.getChainHash());
//            System.out.println(contract.toString());
            Contract contract1 = contractService.insertDocument(contract);
//            准备要返回的数据
            JSONObject resultdata= new JSONObject();
            resultdata.put("ChainHash",contract1.getChainHash());
            resultdata.put("CCSChash",contract1.getCCSCHash());
            resultdata.put("input",jsonObject1);
            resultdata.put("Flag",contract1.getFlag());
//            合约的接口通信均采用链注册的公私钥，合约注册不需要再次生成新的公私钥，仅记录合约接口的公钥即可。
            resultdata.put("Publickey",cc.getClientPublicKey());
//            System.out.println(resultdata.toString());
            Result result= Result.success();
            result.setMsg("注册成功")
                    .setCsignature(
                            RSACipher.sign(
                                    cc.getClientSerectKey(),
                                    resultdata.toString().getBytes()
                            )
                    )
                    .setMsignature(
                            RSACipher.sign(
                                    contract1.getClientSerectKey(),
                                    resultdata.toString().getBytes()
                            )
                    )
                    .setEncryptflag(false)
                    .setData(resultdata.toString());
//            System.out.println(cc.getClientSerectKey());
            return result;

        }else{
            return Result.failure("参数不对，请校验请求参数！");
        }
    }

    @RequestMapping("/subscribe")
    @ApiOperation("订阅")
    public Result subscribe(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
//        System.out.println("订阅访问");
        JSONObject jsonObject1=JSONObject.parseObject(data);
//        检验数据正确性
        Datacheck datacheck=subscribeService;
        Boolean checkdefault = datacheck.checkdefault(jsonObject1);
        System.out.println(jsonObject1.toString());


//        数据检验完  处理插入
        if (checkdefault){
            //        校验签名
            String s  = (String) jsonObject1.getJSONObject("data").get("SChainHash");
            Chain cc=chainService.getDocumentByChainHash(s);
            //获取目标链对象，用来继承其Flag数据
            String s1  = (String) jsonObject1.getJSONObject("data").get("CCSCHash");
            Contract cc1=contractService.getDocumentByCCSCHash(s1);
            Boolean checksignature = chainService.checksignature(jsonObject1,cc.getServerPublicKey());
            if (!checksignature){
                return Result.failure("签名校验失败");
            }
            System.out.println("签名校验成功");
//            格式化json数据，并生成实际的订阅对象。
            Subscribe subscribe=subscribeService.getDocumentByCCSCHash(
                    (String)jsonObject1.getJSONObject("data").get("CCSCHash"),
                    (String)jsonObject1.getJSONObject("data").get("ChainHash")
            );

            if (subscribe == null){
                subscribe=new Subscribe()
                        .setCCSCHash((String)jsonObject1.getJSONObject("data").get("CCSCHash"))
                        .setChainHash((String)jsonObject1.getJSONObject("data").get("ChainHash"))
                        .setSChainHash(new JSONArray())
                        .setStatus((String)jsonObject1.getJSONObject("data").get("Status"))
//                        继承pub接口的Flag   0 无参  1 预参数，半主动  2 实时参数，被动
                        .setFlag(cc1.getFlag()==null?"0":cc1.getFlag())
                        .setExdata((String)jsonObject1.getJSONObject("data").get("Exdata"))
                        .setTime(new Date());
            }

//            System.out.println(contract.toString());

            if (subscribe.getFlag().equals("0")){
                subscribe.addSChainHash((String)jsonObject1.getJSONObject("data").get("SChainHash"));
            }
            else {
                JSONArray jsonArray = jsonObject1.getJSONObject("data").getJSONArray("Preargs");
                subscribe.addSChainHash((String)jsonObject1.getJSONObject("data").get("SChainHash"),jsonArray);
            }

            System.out.println(subscribe.toString());
//          保存更新过后的订阅数据
            boolean b = subscribeService.saveSChainHash(subscribe);
            if(b){

//            准备要返回的数据
                JSONObject resultdata= new JSONObject();
                resultdata.put("ChainHash",subscribe.getChainHash());
                resultdata.put("CCSChash",subscribe.getCCSCHash());
                resultdata.put("input",jsonObject1);
                resultdata.put("Flag",subscribe.getFlag());

                System.out.println(resultdata.toString());
                Result result= Result.success();
                Chain documentByChainHash = chainService.getDocumentByChainHash((String) jsonObject1.getJSONObject("data").get("SChainHash"));
                result.setMsg("注册成功")
                        .setCsignature(
                                RSACipher.sign(
                                        documentByChainHash.getClientSerectKey(),
                                        resultdata.toString().getBytes(StandardCharsets.UTF_8)
                                )
                        )
                        .setMsignature("")
                        .setEncryptflag(false)
                        .setData(resultdata.toString());
                return result;
            }
            return Result.failure("订阅失败，数据保存出错！");


        }else{
            return Result.failure("参数不对，请校验请求参数,请确认合约状态接口是否被注册！");
        }
    }




    @RequestMapping("/test")
    @ApiOperation("测试")
    public Result test(String id){
        if (id==null)
            return Result.failure("请输入name");
        else{
            return Result.success("success");
        }

    }
}
