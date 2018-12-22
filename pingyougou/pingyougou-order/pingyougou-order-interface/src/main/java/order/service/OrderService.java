package order.service;

import entity.TbOrder;
import entity.TbPayLog;
import service.BaseService;
import vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    String addOrder(TbOrder order);

    TbPayLog findPatLoByOutTradeNo(String outTradeNo);

    void updateOrderStatus(String outTradeNo, String transaction_id);
}