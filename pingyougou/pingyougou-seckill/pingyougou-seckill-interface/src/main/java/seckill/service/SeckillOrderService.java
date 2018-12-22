package seckill.service;

import entity.TbSeckillOrder;
import service.BaseService;
import vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    TbSeckillOrder getSeckilOrderInRedisByOrderId(String outTradeNo);

    void saveOrderInRedisToDb(String outTradeNo, String transaction_id);

    void deleteOrderInRedis(String outTradeNo);

    String submitOrder(String username, Long seckillId);
}