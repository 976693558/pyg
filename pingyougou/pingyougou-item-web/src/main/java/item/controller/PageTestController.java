package item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import sellergoods.service.GoodsService;
import sellergoods.service.ItemCatService;
import vo.Goods;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestController {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Reference
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //审核商品后生成商品html页面指定路径
    @GetMapping("/audit")
    public String audit (Long[] goodsIds){
        for(Long goodsId : goodsIds){
            genIteHtml(goodsId);
        }
        return "success";
    }

    private void genIteHtml(Long goodsId) {
        try{
            //获取模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //获取模板需要的数据
            Map<String, Object> dataMap = new HashMap<>();
            //根据商品id查询商品基本信息，描述，sku
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            dataMap.put("goods",goods.getGoods());
            dataMap.put("goodsDesc",goods.getGoodsDesc());

            //查询三级商品分类
            //一级
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataMap.put("itemCat1",itemCat1.getName());
            //二级
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataMap.put("itemCat3",itemCat2.getName());
            //三级
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataMap.put("itemCat3",itemCat3.getName());

            //查询sku商品
            dataMap.put("itemList",goods.getItemList());

            //输出到指定类型
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            FileWriter fileWriter = new FileWriter(filename);
            template.process(dataMap,fileWriter);
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //删除商品后删除指定路径下的商品html页面
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        for (Long goodsId : goodsIds){
            String fileName = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(fileName);
            if(file.exists()){
                file.delete();
            }
        }
        return "success";
    }
}
