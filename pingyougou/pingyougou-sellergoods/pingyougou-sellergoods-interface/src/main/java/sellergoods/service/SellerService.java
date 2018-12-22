package sellergoods.service;

import entity.TbSeller;
import service.BaseService;
import vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    PageResult search(Integer page, Integer rows, TbSeller seller);
}