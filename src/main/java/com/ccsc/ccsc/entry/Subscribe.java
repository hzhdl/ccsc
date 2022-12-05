package com.ccsc.ccsc.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class Subscribe extends Commucation{

    private String SChainHash;
    private String CCSCHash;
    private String Flag;
    private String Status;
    private String Exdata;
    private Date time;

}
