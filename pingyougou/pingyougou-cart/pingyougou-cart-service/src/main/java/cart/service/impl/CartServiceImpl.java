package cart.service.impl;

import cart.service.CartService;
import com.alibaba.dubbo.config.annotation.Service;
import dao.ItemDao;
import entity.TbItem;
import entity.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
    购物车列表：cartList
    购物车：cart
    商家：seller
    商品列表：orderItems
    商品：orderItem
 */

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    //redis中购物车数据
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemDao itemDao;

    /*
        1.验证商品是否存在，商品启用状态是否启用
        2.如果商品对应商家不存在购物车列表，则重新加商家及其对应商品
        3.如果商家存在购物车中，则判断商品是否第一次添加
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.验证商品是否存在，商品启用状态是否启用
        TbItem item = itemDao.selectByPrimaryKey(itemId);
        if(item == null){
            throw new RuntimeException("商品不存在！");
        }
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("商品不可用！");
        }

        String sellerId = item.getSellerId();
        Cart cart = findCartBySellerId(cartList,sellerId);
        System.out.println();
        System.out.println();
        System.out.println("cart = "+cart);
        System.out.println();
        System.out.println();
        if(cart == null){
            if(num > 0){
                //2.如果商品对应商家不存在购物车列表，则重新添加商家及其对应商品
                //添加商家
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                //添加商品
                List<TbOrderItem> orderItems = new ArrayList<>();
                TbOrderItem orderItem = createOrderItem(item,num);
                orderItems.add(orderItem);
                cart.setOrderItemList(orderItems);

                cartList.add(cart);
            }else{
                throw new RuntimeException("购买数量不合法！");
            }
        }else{

            //3.如果商家存在购物车中，则判断商品是否第一次添加
            TbOrderItem orderItem = findOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem != null){
                //不是第一次添加
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                //商品数量小于0，删除商品
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }

                //如果删除商品后购物车没有任何商品则需要将购物车也删除
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }else{
                if(num > 0){
                    orderItem = createOrderItem(item,num);
                    cart.getOrderItemList().add(orderItem);
                }else{
                    throw new RuntimeException("购买数量不合法！");
                }
            }
        }
        return cartList;
    }

    //根据用户名查看购物车
    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if(cartList != null){
            return cartList;
        }
        return new ArrayList<>();
    }

    //已登陆，将商品写入redis中
    @Override
    public void saveCartListByUsername(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,newCartList);
    }

    //合并购物车列表
    @Override
    public List<Cart> mergerCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList) {
        //集合合并，商品不存在的话新增，已存在的话叠加
        for (Cart cart : cookie_cartList){
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for(TbOrderItem orderItem : orderItemList){
                addItemToCartList(redis_cartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redis_cartList;
    }


    //在购物车列表中根据商品id查询对应商品明细
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if(orderItemList != null && orderItemList.size() > 0){
            for (TbOrderItem orderItem : orderItemList){
                if(itemId.equals(orderItem.getItemId())){
                    return orderItem;
                }
            }
        }
        return null;
    }

    //根据商家id在购物车列表中查询购物车
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if(cartList != null && cartList.size() > 0){
            for (Cart cart : cartList){
                if(sellerId.equals(cart.getSellerId())){
                    return cart;
                }
            }
        }
        return null;
    }

    //构造购物车商品明细
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

}
