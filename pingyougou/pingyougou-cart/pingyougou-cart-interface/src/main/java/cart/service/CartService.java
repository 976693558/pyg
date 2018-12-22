package cart.service;

import vo.Cart;

import java.util.List;

public interface CartService {
    //根据商品id查询商品和购买数量加入到cartList
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> findCartListByUsername(String username);

    void saveCartListByUsername(List<Cart> newCartList, String username);

    List<Cart> mergerCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList);
}
