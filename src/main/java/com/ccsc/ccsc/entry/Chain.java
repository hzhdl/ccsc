package com.ccsc.ccsc.entry;


import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@ToString
@Accessors(chain = true)
public class Chain {

    private String ChainHash;
    private String Adress;
    private String ChainID;
    private String PublicKey;
    private String Exdata;
    private String Flag;
    private String Status;

}
