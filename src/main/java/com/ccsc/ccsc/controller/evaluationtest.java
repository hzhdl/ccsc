package com.ccsc.ccsc.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.service.ContractService;
import com.ccsc.ccsc.service.InvocationService;
import com.ccsc.ccsc.util.EResult;
import com.ccsc.ccsc.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.*;

@RestController
@RequestMapping("/evaluation")
@CrossOrigin
@Api(tags = "evaluation", description = "evaluation控制层")
public class evaluationtest {

    @Resource
    InvocationService invocationService;
    @Resource
    ContractService contractService;

    private JSONArray testdata;

    @RequestMapping("/getcurrentinvocations")
    @ApiOperation("获取当前调用记录的数量")
    public EResult getinvoctionnumbers(){
        long invocationnums = invocationService.getInvocationnums();
        return EResult.success(invocationnums);
    }

    @RequestMapping("/getcurrentibynumbers")
    @ApiOperation("获取通过时间来区分批次当前调用记录的数量")
    public EResult getcurrentinvocationnumbersbydate(@RequestBody(required = false) String data){
        JSONObject jsonObject=JSONObject.parseObject(data);
        Date date = jsonObject.getDate("date");
        System.out.println(date);
        long invocationnums = invocationService.getcurrentinvocationsbydate(date);
        return EResult.success(invocationnums);
    }

    @RequestMapping("/getdatares")
    @ApiOperation("获取通过时间来区分批次当前调用记录的数量")
    public EResult getdatares(@RequestBody(required = false) String data){
        JSONObject jsonObject=JSONObject.parseObject(data);
        Date date = jsonObject.getDate("date");
        System.out.println(date);
        List<List<Long>> invocationnums = invocationService.getinvocationbydate(date);
        return EResult.success(invocationnums);
    }
    @RequestMapping("/getcontractaddr")
    @ApiOperation("获取通过指定配置的合约地址")
    public EResult getcontractaddr(@RequestBody(required = false) String data){
        JSONObject jsonObject=JSONObject.parseObject(data);
        System.out.println(jsonObject);
        String chainhash = jsonObject.getString("chainhash");
        Boolean flag = jsonObject.getBoolean("flag");
        Integer NumsLimit = jsonObject.getInteger("NumsLimit");
        Integer ChainLimit = jsonObject.getInteger("ChainLimit");
        List<List<String>> documentByChainHash = new ArrayList<>();
        if (flag){
            documentByChainHash = contractService.getDocumentByChainHash(chainhash);
        }else{
            documentByChainHash = contractService.getDocumentByChainHash0(NumsLimit,ChainLimit,chainhash);
        }
        System.out.println(chainhash+"*****"+documentByChainHash.get(0).size()+"----"+documentByChainHash.get(1).size());
        return EResult.success(documentByChainHash);
    }
    @RequestMapping("/getcontractaddre2")
    @ApiOperation("获取通过指定配置的合约地址")
    public EResult getcontractaddre2(@RequestBody(required = false) String data){
        JSONObject jsonObject=JSONObject.parseObject(data);
        System.out.println(jsonObject);
        String chainhash = jsonObject.getString("chainhash");
        Boolean flag = jsonObject.getBoolean("flag");
        Integer NumsLimit = jsonObject.getInteger("NumsLimit");
        Integer ChainLimit = jsonObject.getInteger("ChainLimit");
        List<List<String>> documentByChainHash = new ArrayList<>();
        if (flag){
            documentByChainHash = contractService.getDocumentByChainHash(chainhash);
        }else{
            documentByChainHash = contractService.getDocumentByChainHash(NumsLimit,ChainLimit,chainhash);
        }
        System.out.println(chainhash+"*****"+documentByChainHash.get(0).size()+"----"+documentByChainHash.get(1).size());
        return EResult.success(documentByChainHash);
    }

    private byte[] getrandombytes(){
        Random random = new Random();
        random.setSeed(new Date().getTime());
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    @RequestMapping("/gennewdata")
    @ApiOperation("获取通过时间来区分批次当前调用记录的数量")
    public EResult gennewdata(@RequestBody(required = false) String data){
        JSONObject jsonObject=JSONObject.parseObject(data);
        JSONArray SmartContractNums = jsonObject.getJSONArray("SmartContractNums");
        for (int i = 0; i < SmartContractNums.size(); i++) {
            SmartContractNums.getJSONObject(i).put("Result", "0x"+String.valueOf(Hex.encode(getrandombytes())));
        }
        System.out.println(SmartContractNums);



        return EResult.success(SmartContractNums);
    }

}
