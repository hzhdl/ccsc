package com.ccsc.ccsc.entry;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class Invocation extends Commucation{

    private String CCSCHash;
    private String ChainHash;
    private String Flag;
    private JSONObject Input;
    private String Msignature;
    private String Result;
    private Date time;

}
