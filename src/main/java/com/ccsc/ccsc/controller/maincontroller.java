package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.util.Result;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.management.relation.Relation;
import java.security.PublicKey;

@RestController
@RequestMapping("/Chain")
@CrossOrigin
@Api(tags = "main", description = "控制层")
public class maincontroller {

    @Resource
    ChainService chainService;

    @RequestMapping("/register")
    @ApiOperation("注册链")
    public Result register(String jsonObject){
        System.out.println(jsonObject);
        JSONObject jsonObject1=JSONObject.parseObject(jsonObject).getJSONObject("data");
        System.out.print(jsonObject1.get("address"));

        return Result.success("success");
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
