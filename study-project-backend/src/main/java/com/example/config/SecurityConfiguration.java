package com.example.config;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;
    @Autowired
    private SecurityAutoConfiguration securityAutoConfiguration;

    @Resource
    DataSource dataSource;

    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http,CorsConfigurationSource corsConfigurationSource,DataSource dataSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(this::onAuthenticationFailure)
                )
                .userDetailsService(authorizeService)
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .rememberMe(remember -> remember
                        //.tokenRepository(tokenRepository)
                        // 可選：自定義 remember-me 參數名稱（默認 "remember-me"）
                        .tokenRepository(new JdbcTokenRepositoryImpl() {{
                            setDataSource(dataSource);
                            setCreateTableOnStartup(true);
                        }})
                        .rememberMeParameter("remember")//改成自用remember
                        // 必須：用於簽名 Token 的私鑰
                        //.key("aVerySecretKey")
                        // 可選：Token 在 Cookie 中的有效期，單位秒（例如 7 天）
                        .tokenValiditySeconds(7 * 24 * 60 * 60)


                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                        .permitAll()
                );

        return http.build();
        // 帳號預設是user
        // 每次跑不太一樣 Using generated security password: 80285cda-89f1-46ba-a588-3ba936064e07

    }

//    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
//
//        return security.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(authorizeService)
//                .build
//    }

//        @Bean
//        public PersistentTokenRepository tokenRepository(DataSource dataSource){
//            JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
//            jdbcTokenRepository.setDataSource(dataSource);
//            jdbcTokenRepository.setCreateTableOnStartup(true);
//            return jdbcTokenRepository;
//        }


        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            // 允许所有来源；生产环境中可指定具体域名或用 addAllowedOriginPattern
            configuration.addAllowedOriginPattern("*");// 上線最好不要這樣用
            // 是否允许携带凭证（Cookie、Authorization header 等）
            configuration.setAllowCredentials(true);
            // 允许的 HTTP 方法
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            // 允许的请求头
            configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
            // 如需暴露给前端的响应头
            configuration.setExposedHeaders(List.of("Authorization"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            // 对所有接口生效
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
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


    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("UTF-8");
        RestBean<?> rest = RestBean.success("登出成功");
        response.getWriter().write(JSONObject.toJSONString(rest, SerializerFeature.PrettyFormat));

    }

//    private void AuthenticationEntrypoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
//        response.setCharacterEncoding("UTF-8");
//        RestBean<?> rest = RestBean.failure(401, exception.getMessage());
//        response.getWriter().write(JSONObject.toJSONString(rest, SerializerFeature.PrettyFormat));
//    }
}
