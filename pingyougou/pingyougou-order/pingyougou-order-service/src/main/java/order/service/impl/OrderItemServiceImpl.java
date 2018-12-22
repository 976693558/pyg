package order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dao.OrderItemDao;
import dao.impl.BaseServiceImpl;
import entity.TbOrderItem;
import order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.util.List;

@Service(interfaceClass = OrderItemService.class)
public class OrderItemServiceImpl extends BaseServiceImpl<TbOrderItem> implements OrderItemService {

    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public PageResult search(Integer page, Integer rows, TbOrderItem orderItem) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(orderItem.get***())){
            criteria.andLike("***", "%" + orderItem.get***() + "%");
        }*/

        List<TbOrderItem> list = orderItemDao.selectByExample(example);
        PageInfo<TbOrderItem> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
