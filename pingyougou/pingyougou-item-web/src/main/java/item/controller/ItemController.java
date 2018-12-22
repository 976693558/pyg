package item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbGoods;
import entity.TbGoodsDesc;
import entity.TbItemCat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import sellergoods.service.GoodsService;
import sellergoods.service.ItemCatService;
import vo.Goods;

@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    //跳转到商品详细页面显示商品
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId){
        ModelAndView mav = new ModelAndView("item");

        //根据商品id查询商品基本信息，描述，已经启用的sku
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");
        TbGoods good = goods.getGoods();
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        //基本信息
        mav.addObject("goods",good);
        //描述
        mav.addObject("goodsDesc",goodsDesc);

        //查询三级商品分类
        //一级
        TbItemCat itemCat1 = new TbItemCat();
        itemCat1 = itemCatService.findOne(good.getCategory1Id());
        mav.addObject("itemCat1"+itemCat1.getName());
        //二级
        TbItemCat itemCat2 = new TbItemCat();
        itemCat2 = itemCatService.findOne(good.getCategory2Id());
        mav.addObject("itemCat2"+itemCat2.getName());
        //三级
        TbItemCat itemCat3 = new TbItemCat();
        itemCat3 = itemCatService.findOne(good.getCategory3Id());
        mav.addObject("itemCat3"+itemCat3.getName());

        //查询sku
        mav.addObject("itemList",goods.getItemList());
        return mav;

    }
}
