package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.entry.Invocation;
import com.ccsc.ccsc.entry.Parsejson;
import com.ccsc.ccsc.entry.Subscribe;
import com.ccsc.ccsc.service.ChainService;
import com.ccsc.ccsc.service.ContractService;
import com.ccsc.ccsc.service.RequestService;
import com.ccsc.ccsc.service.SubscribeService;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

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
    public Result register(@RequestBody(required = false) String data) throws NoSuchAlgorithmException, NoSuchProviderException {
//        获取json参数
        System.out.println(data);
        JSONObject jsonObject1=JSONObject.parseObject(data);

//        首先校验Msignature

//        校验Csignature，注意要将data和Msiganture直接拼接以后进行签名校验。

//        构建Invocation实例
        Parsejson<Invocation> parsejson = new Invocation();
        Invocation invocation = parsejson.parsejsonwithInstance(jsonObject1);
//        通知各个目标链进行更新,主动更新

//        首先取出订阅的ChainHash列表
        Subscribe subscribe = subscribeService.getDocumentByCCSCHash(invocation.getCCSCHash(), invocation.getChainHash());

//        取出订阅的Chain列表
        List<String> sChainHash = subscribe.getSChainHash();

//        通知订阅链上的状态更新
        ArrayList<String> activesync = requestService.activesync(sChainHash, invocation);

        System.out.println(activesync.toString());

//        根据结果将向数据库插入数据


        return Result.failure("test");
//        if (true){
//
//
//        }else{
//            return Result.failure("参数不对，请校验请求参数！");
//        }
    }

}
