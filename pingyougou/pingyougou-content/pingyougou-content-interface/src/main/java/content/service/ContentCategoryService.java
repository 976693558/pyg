package content.service;

import entity.TbContentCategory;
import service.BaseService;
import vo.PageResult;

public interface ContentCategoryService extends BaseService<TbContentCategory> {

    PageResult search(Integer page, Integer rows, TbContentCategory contentCategory);
}