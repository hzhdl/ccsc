package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Contract;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContractService implements Datacheck{
    public static final String Collectionname="Contract";

    @Resource
    ChainService chainService;

    @Resource
    MongoTemplate mongoTemplate;


    public Contract insertDocument(Contract contract){
        return mongoTemplate.insert(contract,Collectionname);
    }

    public  Boolean collectionExists(String Address,String ChainHash,String FunctionName){

        Criteria criteria = Criteria.where("Address").is(Address);
        criteria.and("ChainHash").is(ChainHash);
        criteria.and("FunctionName").is(FunctionName);
        Query query= new Query(criteria);
        List<Contract> contracts = mongoTemplate.find(query, Contract.class, Collectionname);
        return contracts.size() != 0;

    }


    @Override
    public Boolean checkdefault(JSONObject jsonObject) {
//        System.out.println(jsonObject.toString());
        if (jsonObject==null)
            return false;

        if (jsonObject.getJSONObject("data")==null){
            return false;
        }else {
            jsonObject = jsonObject.getJSONObject("data");
            String ChainHash = (String) jsonObject.get("ChainHash");
            String Address= (String) jsonObject.get("Address");
            String FunctionName = (String) jsonObject.get("FunctionName");
            String Flag = (String) jsonObject.get("Flag");
            JSONObject ParamList = (JSONObject) jsonObject.get("ParamList");
            String Publickey = (String) jsonObject.get("Publickey");


//        首先校验是否有空值。
            if (    Address==null||
                    ChainHash == null ||
                    FunctionName == null ||
                    Flag == null ||
                    ParamList == null ||
                    Publickey == null

            ) {
                return false;
            } else if(
                    Address.equals("") ||
                            ChainHash.equals("") ||
                            FunctionName.equals("") ||
                            Flag.equals("") ||
                            Publickey.equals("")
            ){
                return false;
            } else{
                Chain documentByChainHash = chainService.getDocumentByChainHash(ChainHash);
                if (documentByChainHash == null){
                    return false;
                }
                else {
                    Boolean aBoolean = collectionExists(Address,ChainHash,FunctionName);
                    return !aBoolean;
                }
            }
        }
    }

    @Override
    public Boolean checkID(JSONObject jsonObject) {

        return false;
    }




}
