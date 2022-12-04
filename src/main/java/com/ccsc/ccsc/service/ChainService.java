package com.ccsc.ccsc.service;

import com.ccsc.ccsc.entry.Chain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ChainService {

    @Resource
    private MongoTemplate mongoTemplate;

    public static final String Collectionname="Chain";

    public Object getCollectionNames(){
        return mongoTemplate.getCollectionNames();
    }
    public Object collectionExists(String name){
        return mongoTemplate.collectionExists(name);
    }

    public List<Chain> getDocumentALL(){
        List<Chain> documentList = mongoTemplate.findAll(Chain.class, Collectionname);
//        System.out.println(documentList.size()+collectionname);
//        System.out.println(collectionExists(collectionname));
//        System.out.println(getCollectionNames());
        // 输出结果
        for (Chain chain : documentList) {
            System.out.println("用户信息：{"+chain+"}");
        }
        return documentList;
    }
/*
*   根据ChainHash 获取注册的链信息
* */
    public List<Chain> getDocumentByChainHash(String Id){
        Criteria criteria   = Criteria.where("ChainHash").is(Id);
        Query query=new Query(criteria);



        List<Chain> document = mongoTemplate.find(query,Chain.class, Collectionname);
//        System.out.println(documentList.size()+collectionname);
//        System.out.println(collectionExists(collectionname));
//        System.out.println(getCollectionNames());
        // 输出结果
        System.out.println("ByID:"+document);
        return document;
    }
/*
*   根据ChainHash更新数据信息
* */
    public Chain setDocument(String id){
        List<Chain> chain=getDocumentByChainHash(id);

        Chain chain1=new Chain()
                .setChainHash(Integer.toString(new Date().hashCode()))
                .setAdress("dfsfsd")
                .setFlag("s234sdaffsdf")
                .setExdata(id);
        System.out.println(new Date().hashCode());
        //mongoTemplate.save(chain,Collectionname);
        return mongoTemplate.insert(chain1,Collectionname);
    }
}
