package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.entry.Subscribe;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class SubscribeService implements Datacheck{
    @Resource
    ChainService chainService;

    @Resource
    ContractService contractService;

    @Resource
    MongoTemplate mongoTemplate;

    private static String Collectionname="Subscribe";


    /*
     *   根据CCSCHash 获取链信息
     * */
    public Subscribe getDocumentByCCSCHash(String CCSCHash,String ChainHash){
        Criteria criteria   = Criteria.where("CCSCHash").is(CCSCHash);
        criteria.and("ChainHash").is(ChainHash);
        Query query=new Query(criteria);
        List<Subscribe> document = mongoTemplate.find(query, Subscribe.class, Collectionname);
        if (document.size() != 0){
            System.out.println("ByID:"+document.get(0));
            return document.get(0);
        }
        else
            return null;
        // 输出结果

    }
    public boolean saveSChainHash(Subscribe subscribe){
        Criteria criteria =Criteria.where("CCSCHash").is(subscribe.getCCSCHash());
        criteria.and("ChainHash").is(subscribe.getChainHash());
        Query query= new Query(criteria);
        Update update=new Update();
        update.set("SChainHash",subscribe.getSChainHash());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Subscribe.class, Collectionname);
        String resultInfo="";
        boolean b=false;
        if (updateResult.getMatchedCount()>0){
            resultInfo = "共匹配到" + updateResult.getMatchedCount() + "条数据,修改了" + updateResult.getModifiedCount() + "条数据";
            b=true;
        }else {
            Subscribe insert = mongoTemplate.insert(subscribe,Collectionname);
            if (!insert.equals(new Subscribe()) ){
                b=true;
                resultInfo="新订阅插入成功";
            }
            else
                resultInfo="新订阅插入失败";
        }


        System.out.println(resultInfo);
        return b;

    }


    /*{
    code: 1,
    msg: 'success',
    data: {
        ChainHash:"c7953e0a44a812d007ac3e438ca8c77bd8ad6d2c7ac3f47be33dadee37f32da66d83c9202b17d000ac2e1b5ce1b1c5890e0768189750b0b25b59d93c3a835f6c",
        CCSChash:"fee2e9f8c600eb4f07118d56a736de2ad888db00b142933dd910e7069a5b8814d60566e81cb7cc32cc78814420e81823ae6562ba560f37259b15cb04a67b7959",
        SChainHash:"",
        Flag:"",
        Status:"",
        Exdata:""
    },
    count: '',
    exdata: '',
    encryptflag: false,
    Msignature:'',
    Csignature: ''
}*/
    @Override
    public Boolean checkdefault(JSONObject jsonObject) {

        if (jsonObject==null)
            return false;

        if (jsonObject.getJSONObject("data")==null){
            return false;
        }else {
            jsonObject = jsonObject.getJSONObject("data");
            String ChainHash = (String) jsonObject.get("ChainHash");
            String CCSCHash= (String) jsonObject.get("CCSCHash");
            String SChainHash = (String) jsonObject.get("SChainHash");
            String Flag = (String) jsonObject.get("Flag");
            String Status = (String) jsonObject.get("Status");
            String Exdata = (String) jsonObject.get("Exdata");
//        首先校验是否有空值。
            if (    CCSCHash==null||
                    ChainHash == null ||
                    SChainHash == null ||
                    Flag == null ||
                    Status == null ||
                    Exdata == null

            ) {
                return false;
            } else if(
                    CCSCHash.equals("") ||
                            ChainHash.equals("") ||
                            SChainHash.equals("") ||
                            Flag.equals("") ||
                            Status.equals("")
            ){
                return false;
            } else{
                Contract contract=contractService.getDocumentByCCSCHash(CCSCHash);
                Chain chain=chainService.getDocumentByChainHash(SChainHash);
                Subscribe subscribe=getDocumentByCCSCHash(CCSCHash,ChainHash);
                if (contract == null|| !contract.getChainHash().equals(ChainHash)||chain==null){
                    return false;
                }
                else if (subscribe ==null) return true;
                else {
                    List<String> sChainHash = subscribe.getSChainHash();
                    return !sChainHash.contains(SChainHash);
                }
            }
        }
    }

    @Override
    public Boolean checkID(JSONObject jsonObject) {
        return null;
    }
}
