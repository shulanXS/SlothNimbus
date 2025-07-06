package com.yupi.yupicturebackend.config;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求包装过滤器
 *
 * @author pine
 */
@Order(1) // 定义一个过滤器，按照指定的顺序执行
@Component
public class HttpRequestWrapperFilter implements Filter {

    /**
     * 过滤器的主要逻辑方法
     *
     * @param request  原始请求对象
     * @param response 原始响应对象
     * @param chain    过滤器链，用于将请求传递给下一个过滤器或目标资源
     * @throws ServletException 如果过滤过程中发生Servlet异常
     * @throws IOException      如果过滤过程中发生I/O异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 检查请求是否为HTTP请求
        if (request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            // 获取请求的Content-Type头信息
            String contentType = servletRequest.getHeader(Header.CONTENT_TYPE.getValue());
            // 如果Content-Type为JSON，则对请求进行包装
            if (ContentType.JSON.getValue().equals(contentType)) {
                // 可以再细粒度一些，只有需要进行空间权限校验的接口才需要包一层
                // 对请求进行包装，以便于后续处理
                chain.doFilter(new RequestWrapper(servletRequest), response);
            } else {
                // 如果Content-Type不是JSON，直接传递请求和响应对象给下一个过滤器或目标资源
                chain.doFilter(request, response);
            }
        }
    }

}
