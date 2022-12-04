package com.ccsc.ccsc.controller;

import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/Test")
@Api(tags = "测试")
public class Test {
    @Resource
    ChainService chainService;

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
