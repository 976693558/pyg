package manage.service;

import entity.TbSeller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sellergoods.service.SellerService;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService{

    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //根据登陆名到数据库中查询密码
        TbSeller seller = sellerService.findOne(username);
        //已审核通过才能登陆
        if(seller != null && "1".equals(seller.getStatus())){
            return new User(username,seller.getPassword(),authorityList);
        }
        return null;
    }

    public void setSellerService(SellerService sellerService){
        this.sellerService = sellerService;
    }
}
