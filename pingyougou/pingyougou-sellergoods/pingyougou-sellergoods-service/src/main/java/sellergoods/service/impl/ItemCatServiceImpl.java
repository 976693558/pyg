package sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dao.ItemCatDao;
import dao.impl.BaseServiceImpl;
import entity.TbItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import sellergoods.service.ItemCatService;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.util.List;

@Service(interfaceClass = ItemCatService.class)
public class ItemCatServiceImpl extends BaseServiceImpl<TbItemCat> implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public PageResult search(Integer page, Integer rows, TbItemCat itemCat) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(itemCat.get***())){
            criteria.andLike("***", "%" + itemCat.get***() + "%");
        }*/

        List<TbItemCat> list = itemCatDao.selectByExample(example);
        PageInfo<TbItemCat> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
