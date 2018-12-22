package sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dao.SellerDao;
import dao.impl.BaseServiceImpl;
import entity.TbSeller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import sellergoods.service.SellerService;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.util.List;

@Service(interfaceClass = SellerService.class)
public class SellerServiceImpl extends BaseServiceImpl<TbSeller> implements SellerService {

    @Autowired
    private SellerDao sellerDao;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeller seller) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeller.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(seller.getStatus())){
            criteria.andLike("status",  seller.getStatus());
        }
        if(!StringUtils.isEmpty(seller.getNickName())){
            criteria.andLike("nickName",  "%"+seller.getNickName()+"%");
        }
        if(!StringUtils.isEmpty(seller.getName())){
            criteria.andLike("name", "%"+seller.getName()+"%");
        }

        List<TbSeller> list = sellerDao.selectByExample(example);
        PageInfo<TbSeller> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
