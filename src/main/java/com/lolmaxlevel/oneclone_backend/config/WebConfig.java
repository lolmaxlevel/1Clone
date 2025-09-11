package com.lolmaxlevel.oneclone_backend.config;

import com.lolmaxlevel.oneclone_backend.interceptors.IpAddressInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private static final Long MAX_AGE = 3600L;

    @Bean
    //commented out to disable CORS globally
    //to add to cors, you should add frontend IP with port for some reason
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        String allowedIps = System.getenv("ALLOWED_IP_ADDRESSES");
        config.addAllowedOrigin("192.168.3.8");
        config.addAllowedOrigin("192.168.31.109");
        config.addAllowedOrigin("http://192.168.31.109/");
        config.addAllowedOrigin("http://192.168.31.109:5432");
        config.addAllowedOrigin("192.168.3.8:80");
        config.addAllowedOrigin("localhost");
        config.addAllowedOrigin("localhost:80");
        config.addAllowedOrigin("127.0.0.1");
        config.addAllowedOrigin("http://192.168.3.8/");
//        if (allowedIps != null && !allowedIps.isEmpty()) {
//            config.setAllowedOrigins(Arrays.asList(allowedIps.split(",")));
//            log.info("Allowed IP addresses: {}", config.getAllowedOrigins());
//        } else {
//            config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080", "http://localhost:4200", "http://localhost:8081", "192.1"));
//        }
        log.info("Allowed IP addresses: {}", config.getAllowedOrigins());
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.HOST,
                HttpHeaders.ORIGIN,
                HttpHeaders.USER_AGENT
        ));

        config.setExposedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.SET_COOKIE,
                HttpHeaders.CONTENT_DISPOSITION
        ));

        config.setAllowedMethods(Arrays.asList(
                HttpMethod.PATCH.name(),
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));

        config.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IpAddressInterceptor());
    }
}