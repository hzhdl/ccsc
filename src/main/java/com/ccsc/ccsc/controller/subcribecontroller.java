package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/Subcribe")
@CrossOrigin
@Api(tags = "Subcribe", description = "Subcribe控制层")
public class subcribecontroller {

    @Resource
    ChainService chainService;

    @RequestMapping("/register")
    @ApiOperation("注册")
    public Result register(String jsonObject){
        System.out.println(jsonObject);
        JSONObject jsonObject1=JSONObject.parseObject(jsonObject).getJSONObject("data");
        System.out.print(jsonObject1.get("Address"));

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
