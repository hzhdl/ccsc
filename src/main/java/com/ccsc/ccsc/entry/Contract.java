package com.ccsc.ccsc.entry;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class Contract extends Commucation{

    private String ChainHash;
    private String CCSCHash;
    private String FunctionName;
    private String Flag;
    private String ParamList;
    private String RequireParam;
    private String Status;
    private String Exdata;
    private Date time;


}
