package com.ccsc.ccsc.util;




import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


public class MyFilter implements Filter {

    final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MyFilter.class);



//    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 打印请求信息
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//        logger.info("------------- LogFilter 开始 -------------");
//        logger.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
//        logger.info("远程地址: {}", request.getRemoteAddr());
//
//        long startTime = System.currentTimeMillis();
//
//        Enumeration<String> s=request.getParameterNames();
//        System.out.println(s.toString());
//
//        logger.info("------------- LogFilter 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);

        // 如果验证通过执行
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }


    public void destroy() {
    }
}


