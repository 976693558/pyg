package order.service;

import entity.TbOrderItem;
import service.BaseService;
import vo.PageResult;

public interface OrderItemService extends BaseService<TbOrderItem> {

    PageResult search(Integer page, Integer rows, TbOrderItem orderItem);
}