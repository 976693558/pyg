package cart.controller;

import cart.service.CartService;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtils;
import vo.Cart;
import vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //购物车列表名
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    //Cookie购物车最大生存周期 ： 一天
    private static final int Cookie_CART_LIST_MAX_AGE = 60 * 60 * 24;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    //获取当前用户信息
    @GetMapping("getUsername")
    public Map<String,Object> getUsername(){
        Map<String ,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果没有登陆，获得到的用户名为 anonymousUser
        map.put("username",username);
        return map;

    }

    //获取购物车列表数据：如果登陆了则从redis中获取，没登陆则从cookie中获取
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        //判断是否登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //获取cookie中的购物车列表
        String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
        List<Cart> cookie_cartList;
        if(!StringUtils.isEmpty(cartListJsonStr)){
            //将购物车中内容转换为Json格式
            cookie_cartList = JSONArray.parseArray(cartListJsonStr,Cart.class);
        }else{
            cookie_cartList = new ArrayList<>();
        }

        //anonymousUser， 没有登陆时返回的用户名
        if("anonymousUser".equals(username)){
            //没有登陆，返回cookie数据
            return cookie_cartList;
        }else{
            //登陆了，从redis中获取
            List<Cart> redis_cartList = cartService.findCartListByUsername(username);

            //合并购物车列表
            if(cookie_cartList.size() > 0){
                redis_cartList = cartService.mergerCartList(cookie_cartList,redis_cartList);

                //保存最新的购物车列表到redis中
                cartService.saveCartListByUsername(redis_cartList,username);

                //删除原来cookie中购物车列表
                CookieUtils.deleteCookie(request,response,COOKIE_CART_LIST);
            }

            return redis_cartList;
        }
    }

    //实现商品添加购物车列表
    //SpringMVC -> 通过注解设置跨域
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pingyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){

        Result result = Result.fail("加入购物车失败！");
        try{

            //通过代码设置跨域
            //设置允许跨域请求
            //response.setHeader("Access-Control-Allow-Origin","http://item.pingyougou.com");
            //允许携带并接受cookie
            //response.setHeader("Access-Control-Allow-Credentials","true");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入购物车列表
            List<Cart> newCartList =  cartService.addItemToCartList(cartList,itemId,num);

            if("anonymousUser".equals(username)){
                //没登陆，将商品写入到cookie
                String cartListJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request,response,COOKIE_CART_LIST,
                        cartListJsonStr,Cookie_CART_LIST_MAX_AGE,true);
            }else{
                //已登陆，将商品写入redis中
                cartService.saveCartListByUsername(newCartList,username);
            }
            result = Result.ok("加入购物车成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
