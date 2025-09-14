package com.lolmaxlevel.oneclone_backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Simple XSS Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            log.debug("Processing request through simple XSS filter for URI: {}", httpRequest.getRequestURI());
            SimpleXssRequestWrapper wrappedRequest = new SimpleXssRequestWrapper(httpRequest);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        log.info("Simple XSS Filter destroyed");
    }
}