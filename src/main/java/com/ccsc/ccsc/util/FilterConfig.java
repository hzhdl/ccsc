package com.ccsc.ccsc.util;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration //定义此类为配置类,必须增加
public class FilterConfig {

//    @Bean
    public FilterRegistrationBean<MyFilter> myFilterRegistrationBean() {

        FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>(new MyFilter());

        //添加过滤器路径

        filterRegistrationBean.addUrlPatterns("/Chain/*");
        filterRegistrationBean.addUrlPatterns("/Test/*");

//        filterRegistrationBean.addUrlPatterns("/login/*");

        return filterRegistrationBean;

    }

}
