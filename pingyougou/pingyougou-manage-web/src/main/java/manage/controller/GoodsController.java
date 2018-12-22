package manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.TbGoods;
import entity.TbItem;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import search.service.ItemSearchService;
import sellergoods.service.GoodsService;
import vo.PageResult;
import vo.Result;

import javax.jms.*;
import java.util.Collection;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue itemSolrQueue;
    @Autowired
    private ActiveMQQueue itemSolrDeleteQueue;

    @Autowired
    private ActiveMQTopic itemTopic;
    @Autowired
    private ActiveMQTopic itemDeleteTopic;

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbGoods goods) {
        try {
            goodsService.add(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbGoods findOne(Long id) {
        return goodsService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbGoods goods) {
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    //删除商品
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            //删除商品（修改商品spu某个字段）
            goodsService.deleteByIds(ids);
            //删除商品（修改商品sku某个字段）
            //itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
            //删除solr中对应商品的索引数据
            sendMQMsg(itemSolrDeleteQueue,ids);
            //发送商品删除的订阅消息
            sendMQMsg(itemDeleteTopic,ids);

            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    //发送消息到ActiveMQ（Destination是消息类的父类）
    private void sendMQMsg(Destination destination, Long[] ids) {
        try{
            jmsTemplate.send(destination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 分页查询列表
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        String Role = null;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority:authorities){
            Role = authority.toString();
        }
        return goodsService.search(page, rows, goods,Role);
    }

    //审核通过更新状态
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try{
            //先更新状态 , status = 2
            goodsService.updateStatus(ids,status);
            if("2".equals(status)){
                //如果审核通过则需要更新solr索引库数据

                //查询到需要更新的商品列表（通过状态和id查询）
                List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids,"1");
                //常规操作
                //itemSearchService.importItemList(itemList);

                //ActiveMQ操作
                jmsTemplate.send(itemSolrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage message =  session.createTextMessage();
                        message.setText(JSON.toJSONString(itemList));
                        return message;
                    }
                });
                //发送商品审核通过的订阅消息
                sendMQMsg(itemTopic,ids);
            }
            return Result.ok("更新成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("更新失败!");
    }

}
