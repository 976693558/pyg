package sellergoods.service;

import entity.TbGoods;
import entity.TbItem;
import service.BaseService;
import vo.Goods;
import vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods,String Role);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    void isMarkeTable(Long[] ids, String status);

    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    Goods findGoodsByIdAndStatus(Long goodsId, String status);
}