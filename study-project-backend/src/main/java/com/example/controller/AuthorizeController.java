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

    private final String EMAIL_REGEX =  "^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public RestBean<String> validate(@Pattern(regexp = EMAIL_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "e-mail格式不正確") @RequestParam("email") String email, HttpSession session) {

        String s=service.sendVaildateEmail(email,session.getId(),false);

        if(s==null){
            return RestBean.success("e-mail已發送，請注意查收");
        }else {
            return RestBean.failure(400, "e-mail發送失敗，請重新確認e-mail是否正確");
        }
    }

    @PostMapping("/valid-reset-email")
    public RestBean<String> validateResetEmail(@Pattern(regexp = EMAIL_REGEX, flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "e-mail格式不正確") @RequestParam("email") String email, HttpSession session) {

        String s=service.sendVaildateEmail(email,session.getId(),true);
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

    /**
     * 1.發驗證
     * 2.驗證碼是否正確，正確則seesion存標記
     * 3.用戶發起重置密碼請求，如果存在標記則成功重製
     * **/
    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email
                                        , @Length(min=6,max = 6) @RequestParam("code") String code
                                        , HttpSession session){
        String s=service.validateOnly(email,code,session.getId());

        if(s==null){
            session.setAttribute("reset-password",email);
            return RestBean.success();
        }else {
            return RestBean.failure(400, s);
        }
    }

    @PostMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min=6,max = 16) @RequestParam("password") String password
                                          , HttpSession session){
        String email=(String) session.getAttribute("reset-password");
        System.out.println(email);
        System.out.println(password);
        if(email==null){
            return RestBean.failure(401,"請先完成email驗證");
        }else if(service.resetPassword(password,email)){
            session.removeAttribute("reset-password");
//            service.resetPassword(password,email);
            return RestBean.success("密碼重置成功");
        }else{
            return RestBean.failure(500,"內部錯誤，請聯繫管理員23333");
        }

    }

}
