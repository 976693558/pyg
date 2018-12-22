package order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dao.OrderDao;
import dao.OrderItemDao;
import dao.PayLogDao;
import dao.impl.BaseServiceImpl;
import entity.TbOrder;
import entity.TbOrderItem;
import entity.TbPayLog;
import order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;
import util.IdWorker;
import vo.Cart;
import vo.PageResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    //redis中购物车数据的key
    private static  final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private PayLogDao payLogDao;

    //id生成器
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderDao.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    //提交订单
    @Override
    public String addOrder(TbOrder order) {

        //支付日志id，如非微信支付可以为空
        String outTradeNo = "";
        //提取用户对应购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        if(cartList != null && cartList.size() > 0){
            //遍历购物车列表的每个购物车对应生成一个订单和多个其对应订单明细
            //本次应该支付总金额
            double totalFee = 0.0;
            //本次交易的顶顶干id集合
            String orderIds = "";
            for(Cart cart : cartList){
                long orderId = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                //订单来源
                tbOrder.setSourceType(order.getSourceType());
                //购买者
                tbOrder.setUserId(order.getUserId());
                //支付状态 “1”为未付款
                tbOrder.setStatus("1");
                //支付类型
                tbOrder.setPaymentType(order.getPaymentType());
                //收货人手机号
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                //收货人地址
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                //收货人
                tbOrder.setReceiver(order.getReceiver());
                //订单创建时间
                tbOrder.setCreateTime(new Date());
                //订单更新时间
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                //卖家
                tbOrder.setSellerId(cart.getSellerId());

                //本笔订单的支付总金额
                double payment = 0.0;
                //本笔订单明细
                for (TbOrderItem tbOrderItem : cart.getOrderItemList()){
                    tbOrderItem.setId(idWorker.nextId());
                    tbOrderItem.setOrderId(orderId);
                    //累计本笔订单总金额
                    payment += tbOrderItem.getTotalFee().doubleValue();
                    orderItemDao.insertSelective(tbOrderItem);
                }

                tbOrder.setPayment(new BigDecimal(payment));
                orderDao.insert(tbOrder);

                //记录订单id
                if(orderIds.length() > 0){
                    orderIds += ","+orderId;
                }else{
                    orderIds = orderId+"";
                }
                //累计本次所有订单的总金额
                totalFee += payment;
            }

            //如果是微信支付的话则需要生成支付日志保存到数据库中和redis中设置5分钟过期
            if("1".equals(order.getPaymentType())){
                outTradeNo = idWorker.nextId()+"";
                TbPayLog tbPayLog = new TbPayLog();
                tbPayLog.setOutTradeNo(outTradeNo);
                //未支付
                tbPayLog.setUserId("0");
                tbPayLog.setCreateTime(new Date());
                //总金额,取整
                tbPayLog.setTotalFee((long) (totalFee * 100));
                //本次订单id集合
                tbPayLog.setOrderList(orderIds);

                payLogDao.insertSelective(tbPayLog);

            }

            //删除用户对应的购物车列表
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
        //返回支付日志id，如果不是微信支付则返回空
        return outTradeNo;
    }

    //
    @Override
    public TbPayLog findPatLoByOutTradeNo(String outTradeNo) {
        return payLogDao.selectByPrimaryKey(outTradeNo);
    }

    //更新订单信息
    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        //更新支付日志支付状态
        //根据商户订单id找订单
        TbPayLog payLog = findPatLoByOutTradeNo(outTradeNo);
        //已支付
        payLog.setTradeState("1");
        payLog.setPayTime(new Date());
        //设置支付交易号
        payLog.setTransactionId(transaction_id);
        payLogDao.updateByPrimaryKey(payLog);

        //更新支付日志中对应的每一笔订单的支付状态
        String[] orderList = payLog.getOrderList().split(".");

        TbOrder order = new TbOrder();
        order.setPaymentTime(new Date());
        order.setStatus("2");

        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderList));

        orderDao.updateByExampleSelective(order,example);
    }
}
