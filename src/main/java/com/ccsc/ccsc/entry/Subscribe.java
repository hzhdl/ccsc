package com.ccsc.ccsc.entry;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.ccsc.ccsc.util.RSACipher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.core.token.Sha512DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode()
@Data
@ToString
@Accessors(chain = true)
public class Subscribe {


//    被订阅的合约状态接口
    private String ChainHash;
    private String CCSCHash;

    //订阅的链
    private JSONArray SChainHash;

    //    标识一下函数接口的状态
//    标识接口类型
    private String Flag;
//    标识一下函数接口的订阅状态
//    标识当前被订阅的状态
    private String Status;
    //    标识一下函数接口的订阅状态
//    标识当前接口的额外数据，以json形式存储
    private String Exdata;
    private Date time;

    public void addSChainHash(String NewSChainHash,JSONArray args){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ChainHash",NewSChainHash);
        jsonObject.put("preargs",args);
        SChainHash.add(jsonObject);
    }
    public void addSChainHash(String NewSChainHash){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ChainHash",NewSChainHash);
        SChainHash.add(jsonObject);
    }

}
