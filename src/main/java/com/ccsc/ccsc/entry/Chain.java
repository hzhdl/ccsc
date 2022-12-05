package com.ccsc.ccsc.entry;


import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.util.RSACipher;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class Chain extends Commucation implements ObjectFactory {

    private String ChainHash;
    private String Adress;
    private String ChainID;
    private String ChainName;
    private String Exdata;
    private String Flag;
    private String Status;
    private Date time;


    @Override
    public Object getobjectfromfactory(JSONObject jsonObject) {

        Chain chain = (Chain) new Chain()
                .setChainHash("")
                .setChainID((String) jsonObject.get("ChainID"))
                .setAdress((String) jsonObject.get("address"))
                .setExdata("")
                .setChainName((String) jsonObject.get("ChainName"))
                .setTime(new Date())
                .setStatus("0")
                .setFlag("0")
                .setServerPublicKey((String) jsonObject.get("PublicKey"))
                .setClientPublicKey("")
                .setClientSerectKey("");
        return null;
    }
}
