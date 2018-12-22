package seckill.service;

import entity.TbSeckillGoods;
import service.BaseService;
import vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    List<TbSeckillGoods> findList();

    TbSeckillGoods findOneFromRedis(Long id);
}