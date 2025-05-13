package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String EMAIL_REGEX =  "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> validate(@Pattern(regexp = EMAIL_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "e-mail格式不正確") @RequestParam("email") String email, HttpSession session) {

        String s=service.sendVaildateEmail(email,session.getId());

        if(s==null){
            return RestBean.success("e-mail已發送，請注意查收");
        }else {
            return RestBean.failure(400, "e-mail發送失敗，請重新確認e-mail是否正確");
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min=2,max = 8) @RequestParam("username") String username
                                        , @Length(min=6,max = 16) @RequestParam("password") String password
                                        , @RequestParam("email") String email
                                        , @Length(min=6,max = 6) @RequestParam("code") String code
                                        , HttpSession session){


        String s=service.validateAndRegister(username,password,email,code,session.getId());

        if(s==null){
            return RestBean.success("註冊成功");
        }else {
            return RestBean.failure(400, "註冊失敗，驗證碼錯誤");
        }
    }

}
