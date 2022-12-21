package com.ccsc.ccsc.entry;


import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.util.RSACipher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.core.token.Sha512DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class Contract extends Commucation implements Parsejson<Contract>{

    private String ChainHash;
    private String CCSCHash;
    private String FunctionName;
    private String Flag;
    private JSONObject ParamList;
    private String Address;
//    private String RequireParam;
    private String Status;
    private String Exdata;
    private Date time;





    /*
          * {
                code: 1,
                msg: 'success',
                data: {
                    ChainHash:"",
                    FunctionName:"",
                    Flag:"",
                    ParamList:"",
                    Publickey:""
                },
                count: '',
                exdata: '',
                encryptflag: false,
                Msignature:'',
                Csignature: ''
            }
      *
      * */
    @Override
    public Contract parsejsonwithInstance(JSONObject jsonObject) throws NoSuchAlgorithmException, NoSuchProviderException {
        try {
            RSACipher.generateKeyPair();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        String ContractHash= Hex.encodeHexString(Sha512DigestUtils.sha(jsonObject.toString()
                + String.valueOf(new Date().getTime())));
        return (Contract) new Contract()
                .setExdata("")
                .setCCSCHash(ContractHash)
                .setChainHash((String) jsonObject.get("ChainHash"))
                .setFlag((String) jsonObject.get("Flag"))
                .setStatus((String) jsonObject.get("Status")==null?"":(String) jsonObject.get("Status"))
                .setParamList(jsonObject.getJSONObject("ParamList"))
                .setFunctionName((String) jsonObject.get("FunctionName"))
                .setAddress((String) jsonObject.get("Address"))
                .setTime(new Date())
                .setServerPublicKey((String) jsonObject.get("Publickey"))
                .setClientPublicKey(RSACipher.publicKeyString)
                .setClientSerectKey(RSACipher.privateKeyString);
    }
}







