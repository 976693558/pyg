package content.service;

import entity.TbContent;
import service.BaseService;
import vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    List<TbContent> findContentListByCategoryId(Long categoryId);
}