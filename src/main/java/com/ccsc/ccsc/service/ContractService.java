package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.entry.Subscribe;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

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
//            System.out.println("ByID:"+document.get(0));
            return document.get(0);
        }
        else
            return null;
        // 输出结果

    }

    /*
     *   根据CCSCHash 获取注册的链信息
     * */
    public List<List<String>> getDocumentByChainHash(String ChainHash){
        Criteria criteria   = Criteria.where("ChainHash").is(ChainHash);
        criteria.and("Flag").is("0");
        Query query=new Query(criteria);
        List<Contract> document = mongoTemplate.find(query,Contract.class, Collectionname);
        List<String> addr0=new ArrayList<>();
        for (Contract c: document) {
            addr0.add(c.getAddress());
        }

        Criteria criteria1   = Criteria.where("ChainHash").is(ChainHash);
        criteria1.and("Flag").is("1");
        Query query1=new Query(criteria1);
        List<Contract> document1 = mongoTemplate.find(query1,Contract.class, Collectionname);
        List<String> addr1=new ArrayList<>();
        for (Contract c: document1) {
            addr1.add(c.getAddress());
        }

        List<List<String>> res = new ArrayList<>();
        res.add(addr0);
        res.add(addr1);

        return res;
        // 输出结果

    }
    /*
     *   根据CCSCHash 获取注册的链信息
     * */
    public List<List<String>> getDocumentByChainHash(Integer Nlimit,Integer Climit,String ChainHash){
        Criteria criteria   = Criteria.where("ChainHash").is(ChainHash);
        criteria.and("Flag").is("0");
        Query query=new Query(criteria);
//        query.limit(Nlimit);
        List<Contract> document = mongoTemplate.find(query,Contract.class, Collectionname);
        System.out.println("----------------"+document.size());
        List<Contract> documentt = new ArrayList<>();
        for (Contract c : document) {
            Subscribe documentByCCSCHash = getDocumentByCCSCHash(c.getCCSCHash(), c.getChainHash());
            if(documentByCCSCHash.getSChainHash().size()==Climit){
                documentt.add(c);
            }
        }
        List<String> addr0=new ArrayList<>();
        for (Contract c: documentt) {
            addr0.add(c.getAddress());
        }

        Criteria criteria1   = Criteria.where("ChainHash").is(ChainHash);
        criteria1.and("Flag").is("1");
        Query query1=new Query(criteria1);
//        query1.limit(Nlimit);
        List<Contract> document1 = mongoTemplate.find(query1,Contract.class, Collectionname);
        System.out.println("----------------"+document1.size());

        List<Contract> documentt1 = new ArrayList<>();
        for (Contract c : document1) {
            Subscribe documentByCCSCHash = getDocumentByCCSCHash(c.getCCSCHash(), c.getChainHash());
            if(documentByCCSCHash.getSChainHash().size()==Climit){
                documentt1.add(c);
            }
        }
        List<String> addr1=new ArrayList<>();
        for (Contract c: documentt1) {
            addr1.add(c.getAddress());
        }

        List<List<String>> res = new ArrayList<>();
        res.add(addr0);
        res.add(addr1);

        return res;
        // 输出结果

    }
    /*
     *   根据CCSCHash 获取注册的链信息
     * */
    public List<List<String>> getDocumentByChainHash0(Integer Nlimit,Integer Climit,String ChainHash){
        Criteria criteria   = Criteria.where("ChainHash").is(ChainHash);
        criteria.and("Flag").is("0");
        Query query=new Query(criteria);
        query.limit(Nlimit);
        List<Contract> document = mongoTemplate.find(query,Contract.class, Collectionname);
        System.out.println("----------------"+document.size());
        List<Contract> documentt = new ArrayList<>();
        for (Contract c : document) {
            Subscribe documentByCCSCHash = getDocumentByCCSCHash(c.getCCSCHash(), c.getChainHash());
            if(documentByCCSCHash.getSChainHash().size()==Climit){
                documentt.add(c);
            }
        }
        List<String> addr0=new ArrayList<>();
        for (Contract c: documentt) {
            addr0.add(c.getAddress());
        }

        Criteria criteria1   = Criteria.where("ChainHash").is(ChainHash);
        criteria1.and("Flag").is("1");
        Query query1=new Query(criteria1);
        query1.limit(Nlimit);
        List<Contract> document1 = mongoTemplate.find(query1,Contract.class, Collectionname);
        System.out.println("----------------"+document1.size());

        List<Contract> documentt1 = new ArrayList<>();
        for (Contract c : document1) {
            Subscribe documentByCCSCHash = getDocumentByCCSCHash(c.getCCSCHash(), c.getChainHash());
            if(documentByCCSCHash.getSChainHash().size()==Climit){
                documentt1.add(c);
            }
        }
        List<String> addr1=new ArrayList<>();
        for (Contract c: documentt1) {
            addr1.add(c.getAddress());
        }

        List<List<String>> res = new ArrayList<>();
        res.add(addr0);
        res.add(addr1);

        return res;
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
            JSONArray ParamList = jsonObject.getJSONArray("ParamList");
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

    /*
     *   根据CCSCHash 获取链信息
     * */
    public Subscribe getDocumentByCCSCHash(String CCSCHash,String ChainHash){
        Criteria criteria   = Criteria.where("CCSCHash").is(CCSCHash);
        criteria.and("ChainHash").is(ChainHash);
        Query query=new Query(criteria);
        List<Subscribe> document = mongoTemplate.find(query, Subscribe.class, "Subscribe");
        if (document.size() != 0){
            System.out.println("ByID:"+document.get(0));
            return document.get(0);
        }
        else
            return null;
        // 输出结果

    }




}
