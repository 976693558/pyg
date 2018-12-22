package item.activemq.listener;

import entity.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import sellergoods.service.GoodsService;
import sellergoods.service.ItemCatService;
import vo.Goods;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ItemAuditMessageListener extends
        AbstractAdaptableMessageListener{

    @Value("ITEM_HTML_PATH")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Reference
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        for(Long goodsId : goodsIds){
            genItemHtml(goodsId);
        }
        System.out.println("同步生成商品静态页面成功");
    }

    private void genItemHtml(Long goodsId) {
        try{
            //获取模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //获取模板需要的数据
            Map<String,Object> dataModel = new HashMap<>();
            //根据id查询商品基本信息，描述，和sku列表
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

            //基本信息（spu）
            dataModel.put("goods",goods.getGoods());

            //描述信息
            dataModel.put("goodsDesc",goods.getGoodsDesc());

            //获取三级分类
            TbItemCat category1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            TbItemCat category2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            TbItemCat category3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("category1",category1);
            dataModel.put("category2",category2);
            dataModel.put("category3",category3);

            //商品sku列表
            dataModel.put("itemList",goods.getItemList());

            //输出到指定路径
            String fileName = ITEM_HTML_PATH+goodsId+".html";
            FileWriter fileWriter = new FileWriter(fileName);
            template.process(dataModel,fileWriter);
            fileWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
