package shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

    //认证当前登陆人信息
    @GetMapping("getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map = new HashMap<>();
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
