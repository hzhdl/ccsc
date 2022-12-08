package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Parsejson;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.service.CheckSign;
import com.ccsc.ccsc.service.Datacheck;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import javax.annotation.Resource;
import javax.management.relation.Relation;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/Chain")
@CrossOrigin
@Api(tags = "Chain", description = "Chain控制层")
public class maincontroller {

    @Resource
    ChainService chainService;



    @Resource
    CheckSign checkSign;

    @RequestMapping("/register")
    @ApiOperation("注册链")
    public Result register(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {

//      注册不需要校验签名，仅需校验数据格式
//        System.out.println(data);
        JSONObject jsonObject=JSONObject.parseObject(data);
        System.out.println(jsonObject.toString());

//        校验数据格式，是否可以插入
        Datacheck datacheck = chainService;
        Boolean result=datacheck.checkdefault(jsonObject);
        if (result){
            Parsejson parsejson = new Chain() ;
            Chain chain=(Chain) parsejson.parsejsonwithInstance(jsonObject);
            if (chain == null){
                return Result.success("服务器错误，请稍后重试！");
            }
            Chain chain1 = chainService.insertDocument(chain);
            JSONObject resultdata= new JSONObject();
            resultdata.put("ChainHash",chain1.getChainHash());
            resultdata.put("input",jsonObject);
            resultdata.put("status",chain1.getStatus());
            resultdata.put("Publickey",chain1.getClientPublicKey());
            System.out.println(resultdata.toString());

            Result success=new Result()
                    .setMsg("注册成功")
                    .setCode(0)
                    .setExdata("")
                    .setEncryptflag(false)
                    .setCount("")
                    .setData(resultdata)
                    .setMsignature("")
                    .setCsignature("");
            success.setCsignature(
                    RSACipher.sign(chain1.getClientSerectKey(),
                            resultdata.toString().getBytes()
                    )
            );
            byte[] b=Base64Utils.decodeFromString(success.getCsignature());

            System.out.println(RSACipher.checkSign(chain1.getClientPublicKey(),resultdata.toString().getBytes(),success.getCsignature()));
            return success;
        }
        else{
            return Result.success("链已被注册，请检查你的域名和chainID");
        }
    }

    @RequestMapping("/update")
    @ApiOperation("更新链信息")
    public Result update(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {

        return Result.success("更新成功");
    }




    @RequestMapping("/test")
    @ApiOperation("测试")
    public Result test(@RequestBody(required = false) String data) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String id = (String) jsonObject.get("id");
        JSONObject resultdata= new JSONObject();
        resultdata.put("ChainHash","");
        resultdata.put("input","");
        resultdata.put("status","0");
        resultdata.put("Publickey","");
        byte[] bytes = resultdata.toJSONString().getBytes(StandardCharsets.UTF_8);
        System.out.println(resultdata.toJSONString());
        System.out.println(Arrays.toString(bytes));
        System.out.println(RSACipher.sign(id, bytes));

        return Result.success(RSACipher.sign(id, bytes));
    }
}
