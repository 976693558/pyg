package user.service;


import entity.TbUser;
import service.BaseService;
import vo.PageResult;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    void sendSmsCode(String phone);

    boolean checkSmsCode(String phone, String smsCode);
}