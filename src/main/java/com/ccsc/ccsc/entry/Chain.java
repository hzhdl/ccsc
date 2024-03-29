package com.ccsc.ccsc.entry;


import com.alibaba.fastjson.JSONObject;
import com.ccsc.ccsc.util.RSACipher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.util.Base64Utils;


import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Accessors(chain = true)
public class Chain extends Commucation implements Parsejson<Chain> {

    private String ChainHash;
    private String Address;
    private String ChainID;
    private String ChainName;
    private String Exdata;
    private String Flag;
    private String Status;
    private Date time;


    public JSONObject toJSONString() {
        return JSONObject
                .parseObject("{" +
                "ChainHash:'" + ChainHash + '\'' +
                ", Address:'" + Address + '\'' +
                ", ChainID:'" + ChainID + '\'' +
                ", ChainName:'" + ChainName + '\'' +
                ", Exdata:'" + Exdata + '\'' +
                ", Flag:'" + Flag + '\'' +
                ", Status:'" + Status + '\'' +
                ", time:'" + time.getTime() + '\'' +
                ", clientPublicKey:'" + ClientPublicKey + '\'' +
                "}");
    }

    /*
            * {
                code: 1,
                msg: 'success',
                data: {
                    address:"http://172.16.0.13:3000",
                    ChainName:"ETH",
                    ChainID:"11",
                    Publickey:"",
                },
                count: null,
                exdata: '',
                encryptflag: false,
                Msignature:'',
                Csignature: ''
            }
        *
        * */
    @Override
    public Chain parsejsonwithInstance(JSONObject jsonObject) throws NoSuchAlgorithmException, NoSuchProviderException {
        try {
            RSACipher.generateKeyPair();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        String chainhash= Hex.encodeHexString(Sha512DigestUtils.sha(jsonObject.toString()
                + String.valueOf(new Date().getTime())));
        return (Chain) new Chain()
                .setChainHash(chainhash)
                .setExdata(jsonObject.get("Exdata") == null ?"":(String) jsonObject.get("Exdata"))
                .setAddress((String) jsonObject.get("Address"))
                .setFlag(jsonObject.get("Flag") == null ?"0":(String) jsonObject.get("Flag"))
                .setChainID((String) jsonObject.get("ChainID"))
                .setChainName((String) jsonObject.get("ChainName"))
                .setStatus(jsonObject.get("Status") == null ?"0":(String) jsonObject.get("Status"))
                .setTime(new Date())
                .setServerPublicKey((String) jsonObject.get("Publickey"))
                .setClientPublicKey(RSACipher.publicKeyString)
                .setClientSerectKey(RSACipher.privateKeyString);
    }
}
