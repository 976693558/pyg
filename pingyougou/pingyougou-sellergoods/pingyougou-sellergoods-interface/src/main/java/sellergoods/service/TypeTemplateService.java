package sellergoods.service;

import entity.TbTypeTemplate;
import service.BaseService;
import vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    List<Map> findSpecList(Long id);
}