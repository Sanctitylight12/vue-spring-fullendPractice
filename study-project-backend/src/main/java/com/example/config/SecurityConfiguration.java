package com.example.config;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.entity.RestBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this::onAuthenticationFailure)
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                );

        return http.build();
        // 帳號預設是user
        // 每次跑不太一樣 Using generated security password: 80285cda-89f1-46ba-a588-3ba936064e07

    }


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("UTF-8");
        RestBean<?> rest = RestBean.success("登入成功");
        response.getWriter().write(JSONObject.toJSONString(rest, SerializerFeature.PrettyFormat));
    }


    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("UTF-8");
        RestBean<?> rest = RestBean.failure(401, exception.getMessage());
        response.getWriter().write(JSONObject.toJSONString(rest, SerializerFeature.PrettyFormat));
    }

//    private void AuthenticationEntrypoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
//        response.setCharacterEncoding("UTF-8");
//        RestBean<?> rest = RestBean.failure(401, exception.getMessage());
//        response.getWriter().write(JSONObject.toJSONString(rest, SerializerFeature.PrettyFormat));
//    }
}
