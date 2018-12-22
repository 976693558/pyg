package sellergoods.service;

import entity.TbSpecificationOption;
import service.BaseService;
import vo.PageResult;

public interface SpecificationOptionService extends BaseService<TbSpecificationOption> {

    PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption);
}