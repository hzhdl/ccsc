package com.ccsc.ccsc.service;

import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.entry.Chain;
import com.ccsc.ccsc.entry.Contract;
import com.ccsc.ccsc.entry.Invocation;
import com.ccsc.ccsc.entry.Subscribe;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvocationService {
    @Resource
    ChainService chainService;

    @Resource
    ContractService contractService;

    @Resource
    MongoTemplate mongoTemplate;

    private static String Collectionname="Invocation";



    public boolean saveInvocation(Invocation invocation){

        boolean b=false;
        Invocation insert = mongoTemplate.insert(invocation, Collectionname);
        String resultInfo;
        if (!insert.equals(new Invocation()) ){
            b=true;
            resultInfo="新访问记录插入成功";
        }
        else
            resultInfo="新访问记录插入失败";

        System.out.println(resultInfo);
        return b;

    }

    public long getInvocationnums(){

        boolean b=false;
        Query query = new Query();
        long count = mongoTemplate.count(query, Collectionname);
        System.out.println("当前调用数为："+count);
        return count;

    }
    public long getcurrentinvocationsbydate(Date date){

        boolean b=false;
        Criteria criteria= Criteria.where("ServerStime").gt(date.getTime()).lt(new Date().getTime());

//        criteria.is(date);
        Query query = new Query(criteria);
        long count = mongoTemplate.count(query, Collectionname);
        System.out.println("当前调用数为："+count);
        return count;

    }
    public List<List<Long>> getinvocationbydate(Date date){

        boolean b=false;
        Criteria criteria= Criteria.where("ServerStime").gt(date.getTime()).lt(new Date().getTime());

//        criteria.is(date);
        Query query = new Query(criteria);
        List<Invocation> invocations = mongoTemplate.find(query,Invocation.class, Collectionname);

        List<List<Long>> timelist = new ArrayList<>();
        List<Long> timelist0= new ArrayList<>();
        List<Long> timelist1= new ArrayList<>();
        List<Long> arraytime= new ArrayList<>();
        List<Long> singletime= new ArrayList<>();
        List<Long> arraysuccess= new ArrayList<>();
        Long count0 = 0L;
        Long count1 = 0L;
        Long at0  = 0L;
        Long at1  = 0L;
        Long tt0  = 0L;
        Long tt1  = 0L;

        for (Invocation i :
                invocations) {
            if (i.getFlag().equals("0")){
                timelist0.add(i.getTime().getJSONObject(0).getLong("Maxtime"));
//                System.out.println(i.getTime().getJSONObject(0).getLong("Maxtime"));
                at0+=i.getTime().getJSONObject(0).getLong("Maxtime");
                tt0+=i.getTime().getJSONObject(0).getLong("Signletime");
                count0+=i.getResponselist().size();
            }
            else {
                timelist1.add(i.getTime().getJSONObject(0).getLong("Maxtime"));
                at1+=i.getTime().getJSONObject(0).getLong("Maxtime");
                tt1+=i.getTime().getJSONObject(0).getLong("Signletime");
                count0+=i.getResponselist().size();
            }
        }
        arraytime.add(at0/(timelist0.size()==0?1:timelist0.size()));
        arraytime.add(at1/(timelist1.size()==0?1:timelist1.size()));
        singletime.add(tt0/(timelist0.size()==0?1:timelist0.size()));
        singletime.add(tt1/(timelist1.size()==0?1:timelist1.size()));
        arraysuccess.add(count0);
        arraysuccess.add(count1);
        timelist.add(timelist0);
        timelist.add(timelist1);
        timelist.add(arraytime);
        timelist.add(singletime);
        timelist.add(arraysuccess);
//        System.out.println("当前调用数为："+arraytime);
        return timelist;

    }




}
