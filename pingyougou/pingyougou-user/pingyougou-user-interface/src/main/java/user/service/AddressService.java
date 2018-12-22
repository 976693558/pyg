package user.service;

import entity.TbAddress;
import service.BaseService;
import vo.PageResult;

public interface AddressService extends BaseService<TbAddress> {

    PageResult search(Integer page, Integer rows, TbAddress address);
}