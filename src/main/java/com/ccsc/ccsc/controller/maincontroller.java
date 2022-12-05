package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.ObjectFactory;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.service.CheckSign;
import com.ccsc.ccsc.service.Datacheck;
import com.ccsc.ccsc.util.Result;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.management.relation.Relation;
import java.security.PublicKey;

@RestController
@RequestMapping("/Chain")
@CrossOrigin
@Api(tags = "Chain", description = "Chain控制层")
public class maincontroller {

    @Resource
    ChainService chainService;

    @Resource
    Datacheck datacheck = chainService;

    @Resource
    Chain chain;

    @Resource
    ObjectFactory objectFactory = chain;

    @Resource
    CheckSign checkSign;

    @RequestMapping("/register")
    @ApiOperation("注册链")
    public Result register(@RequestBody(required = false) String data){

//      注册不需要校验签名，仅需校验数据格式
//        System.out.println(data);
        JSONObject jsonObject=JSONObject.parseObject(data);

//        校验数据格式，是否可以插入
        Boolean result=datacheck.checkdefault(jsonObject);
        if (result){
            chainService.insertDocument((Chain) objectFactory.getobjectfromfactory(jsonObject));
            return Result.failure();
        }
        else{
            Result failure = Result.failure("");
            failure.setMsg("链已被注册，请检查你的域名和chainID");
            return failure;
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
