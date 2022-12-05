package com.ccsc.ccsc.entry;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.annotation.Resource;

@ToString
@Setter
@Getter
@Accessors(chain = true)
public class Commucation {

    public  String ClientPublicKey;
    public  String ClientSerectKey;
    public  String ServerPublicKey;
}
