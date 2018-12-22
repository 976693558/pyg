package search.service;

import entity.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService{
    Map<String,Object> search(Map<String, Object> searchMap);

    //商品更新后同步索引库
    void importItemList(List<TbItem> itemList);

    //商品删除后同步索引库
    void deleteItemByGoodsIdList(List<Long> goodsIdList);
}
