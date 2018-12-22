package sellergoods.service;

import entity.TbItemCat;
import service.BaseService;
import vo.PageResult;

public interface ItemCatService extends BaseService<TbItemCat> {

    PageResult search(Integer page, Integer rows, TbItemCat itemCat);
}