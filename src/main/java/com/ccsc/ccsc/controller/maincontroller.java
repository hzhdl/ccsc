package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Parsejson;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.service.CheckSign;
import com.ccsc.ccsc.service.Datacheck;
import com.ccsc.ccsc.util.RSACipher;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Date;

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
//        签名校验
        Date date = new Date();
        Boolean b1 = chainService.checksignature(jsonObject);
        Date date1 = new Date();
        System.out.println(date1.compareTo(date));

        if (result&&b1){
            System.out.println("签名校验成功，数据检验成功");
            Parsejson<Chain> parsejson = new Chain() ;
            Chain chain= parsejson.parsejsonwithInstance(jsonObject);
            if (chain == null){
                return Result.failure("服务器错误，请稍后重试！");
            }
            Chain chain1 = chainService.insertDocument(chain);
            JSONObject resultdata= new JSONObject();
            resultdata.put("ChainHash",chain1.getChainHash());
            resultdata.put("input",jsonObject);
            resultdata.put("status",chain1.getStatus());
            resultdata.put("Publickey",chain1.getClientPublicKey());
//            System.out.println(resultdata.toString());
            System.out.println(chain1.toString());
            Result success=new Result()
                    .setMsg("注册成功")
                    .setCode(0)
                    .setExdata("")
                    .setEncryptflag(false)
                    .setCount("")
                    .setData(resultdata.toString())
                    .setMsignature("")
                    .setCsignature("");
            success.setCsignature(
                    RSACipher.sign(chain1.getClientSerectKey(),
                            resultdata.toString().getBytes()
                    )
            );
            byte[] b=Base64Utils.decodeFromString(success.getCsignature());

//            System.out.println(RSACipher.checkSign(chain1.getClientPublicKey(),resultdata.toString().getBytes(),success.getCsignature()));
            return success;
        }
        else if (!result){
            return Result.failure("链已被注册，请检查你的域名和chainID，如丢失相关注册数据，请联系相关管理员。");
        }else {
            return Result.failure("请检查您的签名和公钥是否匹配！");
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
