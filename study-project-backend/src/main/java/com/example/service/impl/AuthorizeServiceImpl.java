package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate stringRedisTemplate;


    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(username==null){
            throw new UsernameNotFoundException("用戶名不為空");
        }
        Account account=mapper.findAccountByNameOrEmail(username);

        if(account==null){
            throw new UsernameNotFoundException("用戶名或密碼錯誤");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();

    }


    /**
     * 1.生成驗證碼
     * 2.email和驗證碼，存在redis，過期時間為三分鐘
     * 3.發送驗證碼到指定email
     * 4.如果發送失敗，把redis裡面的剛剛插入的刪除
     * 5.用戶在註冊，把資料從redis拿出來key-value，驗證是否一致
     *
     */
    @Override
    public String sendVaildateEmail(String email,String sessionId) {
        String key="email:"+sessionId+ ":" +email;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
           Long expire= Optional.ofNullable(stringRedisTemplate.getExpire(key,TimeUnit.SECONDS)).orElse(0L);
           if(expire>120) return "請求頻繁，請稍後再試";
        }

        if(mapper.findAccountByNameOrEmail(email)!=null){
            return "此e-mail，已經註冊了!";

        }

        Random random=new Random();
        int code= random.nextInt(899999) + 100000;
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的驗證郵件");
        message.setText("驗證碼"+code);
        try{
            mailSender.send(message);

            //**
            stringRedisTemplate.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
            e.printStackTrace();
            return "驗證碼發送失敗，請確認e-mail是否正確";
        }

    }


    @Override
    public String validateAndRegister(String username,String password,String email,String code,String sessionId){
        String key="email:"+sessionId+ ":" +email;

        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            String s=stringRedisTemplate.opsForValue().get(key);
            if(s==null){return "驗證碼過期，請重新獲得驗證碼";}

            if(s.equals(code)){
                password=encoder.encode(password);
                if(mapper.createAccount(username, password, email)>0){
                    return null;
                }else{
                    return "內部錯誤，請聯繫管理員";
                }
            }else {
                return "驗證碼錯誤，請確認後再提交";
            }
        }else {
            return "請先獲得驗證碼驗證";
        }
    }



}
