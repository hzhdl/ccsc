package com.ccsc.ccsc.entry;


import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.util.RSACipher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.util.Base64Utils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

@EqualsAndHashCode()
@Data
@ToString
@Accessors(chain = true)
public class Invocation implements Parsejson<Invocation>{

    private String CCSCHash;
    private String ChainHash;
//    标识是否是主动，0主动同步，1被动调用
    private String Flag;
//    标识调用的输入，如果是主动，则可为空{}
    private JSONObject Input;
//    对消息的签名
    private String Msignature;
    private JSONObject Result;
    private Date time;


    @Override
    public Invocation parsejsonwithInstance(JSONObject jsonObject) throws NoSuchAlgorithmException, NoSuchProviderException {

        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
        String chainhash= Base64Utils.encodeToString(Sha512DigestUtils.sha(jsonObject.toString()
                + String.valueOf(new Date().getTime())));
        return new Invocation()
                .setCCSCHash((String) jsonObject1.get("CCSCHash"))
                .setChainHash((String) jsonObject1.get("ChainHash"))
                .setResult((JSONObject) jsonObject1.get("Result"))
                .setInput(jsonObject1)
                .setTime(new Date())
                .setFlag((String) jsonObject1.get("Flag"))
                .setMsignature((String) jsonObject.get("Msignature"));

    }
}
