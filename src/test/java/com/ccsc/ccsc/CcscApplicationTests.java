package com.ccsc.ccsc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
class CcscApplicationTests {

    @Resource
    testthread testthreadservice;

    @Test
    void contextLoads() {
//        Long test = testthreadservice.test();
//        System.out.println(test);
        ArrayList<Integer> test = test();
        System.out.println(test);

    }

    public  ArrayList<Integer> test(){
        List<CompletableFuture<Integer>> arrayList=new ArrayList<CompletableFuture<Integer>>();
        ArrayList<Integer> arrayList1=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            arrayList.add(testthreadservice.executeAsyncTask(i));
            arrayList1.add(finalI);
        }
        CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[]{})).join();
        return arrayList1;
    }

}
