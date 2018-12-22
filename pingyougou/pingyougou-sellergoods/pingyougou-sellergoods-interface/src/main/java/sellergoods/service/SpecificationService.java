package sellergoods.service;

import entity.TbSpecification;
import service.BaseService;
import vo.PageResult;
import vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    void add(Specification specification);

    Specification findOne(Long id);

    void update (Specification specification);

    void deleteSpecificationByIds(Long[] ids);

    List<Map<String,Object>> selectOptionList();
}