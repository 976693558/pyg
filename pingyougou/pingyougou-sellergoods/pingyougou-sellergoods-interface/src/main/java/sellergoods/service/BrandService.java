package sellergoods.service;

import entity.TbBrand;
import service.BaseService;
import vo.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {

    //查询全部品牌
    List<TbBrand> findAllBrand();

    List<TbBrand> testPage(Integer pageNum, Integer pageSize);

    PageResult search( TbBrand tbBrand, Integer pageNum, Integer pageSize);

    List<Map<String,Object>> selectOptionList();
}
