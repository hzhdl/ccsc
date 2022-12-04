package com.ccsc.ccsc.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "spring.encrypt")
//考虑从数据库读取key
@Component
public class EncryptProperties {
    // 这一块一定要16位或者整数倍，最多256
    private final static String DEFAULT_KEY = "www.shawn222.com";
    private String key = DEFAULT_KEY;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
