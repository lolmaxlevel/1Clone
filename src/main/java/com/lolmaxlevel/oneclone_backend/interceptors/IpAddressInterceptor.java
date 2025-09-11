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

    List<String> allowedIpAddresses;

    public IpAddressInterceptor() {
        String allowedIps = System.getenv("ALLOWED_IP_ADDRESSES");
        log.info("Allowed IP addresses: {}", allowedIps);
        if (allowedIps != null && !allowedIps.isEmpty()) {
            allowedIpAddresses = Arrays.asList(allowedIps.split(","));
        } else {
            allowedIpAddresses = Arrays.asList("192.168.3.8", "127.0.0.1", "192.168.0.106", "192.168.31.109"); // default IP addresses
        }
    }

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        log.info("IP Address: {}", ipAddress);
        if (allowedIpAddresses.contains(ipAddress)) {
            return true;
        } else {
            log.warn("Forbidden IP address: {}", ipAddress);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }
}