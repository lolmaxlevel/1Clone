package com.lolmaxlevel.oneclone_backend.interceptors;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;


import java.util.Arrays;
import java.util.List;

@Slf4j
public class IpAddressInterceptor implements HandlerInterceptor {

    // #TODO get allowed IP addresses from the configuration
    List<String> allowedIpAddresses = Arrays.asList("192.168.3.8", "127.0.0.1", "192.168.0.106"); // allowed IP addresses

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        log.info("IP Address: {}", request.getRemoteAddr());
        String ipAddress = request.getRemoteAddr();
        if (allowedIpAddresses.contains(ipAddress)) {
            return true;
        } else {
            log.warn("Forbidden IP address: {}", ipAddress);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }
}