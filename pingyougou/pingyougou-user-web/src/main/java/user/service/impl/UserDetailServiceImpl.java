package user.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService{
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //用户认证的工作已经交由cas认证，当前类只做查询用户权限
        //创建用户
        List<GrantedAuthority> authorities = new ArrayList<>();
        //添加用户权限
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(username,"",authorities);
    }
}
