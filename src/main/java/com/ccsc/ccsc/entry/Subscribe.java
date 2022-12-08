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

//    被订阅的合约状态接口
    private String ChainHash;
    private String CCSCHash;

    //订阅的链
    private String SChainHash;

    //    标识一下函数接口的状态
    private String Flag;
//    标识一下函数接口的订阅状态
    private String Status;
    //    标识一下函数接口的订阅状态
    private String Exdata;
    private Date time;



}
