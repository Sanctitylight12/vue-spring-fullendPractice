package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String EMAIL_REGEX =  "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> validate(@Pattern(regexp = EMAIL_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "e-mail格式不正確") @RequestParam("email") String email, HttpSession session) {
        if(service.sendVaildateEmail(email,session.getId())){
            return RestBean.success("e-mail已發送，請注意查收");
        }else {
            return RestBean.failure(400, "e-mail發送失敗，請重新確認e-mail是否正確");
        }
    }

}
