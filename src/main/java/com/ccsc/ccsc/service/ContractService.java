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

    /*
     *   根据CCSCHash 获取注册的链信息
     * */
    public Contract getDocumentByCCSCHash(String Id){
        Criteria criteria   = Criteria.where("CCSCHash").is(Id);
        Query query=new Query(criteria);
        List<Contract> document = mongoTemplate.find(query,Contract.class, Collectionname);
        if (document.size() != 0){
            System.out.println("ByID:"+document.get(0));
            return document.get(0);
        }
        else
            return null;
        // 输出结果

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
            JSONObject ParamList = jsonObject.getJSONObject("ParamList");
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
