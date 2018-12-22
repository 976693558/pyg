package sellergoods.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dao.*;
import dao.impl.BaseServiceImpl;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sellergoods.service.GoodsService;
import tk.mybatis.mapper.entity.Example;
import vo.Goods;
import vo.PageResult;

import java.util.*;

@Service(interfaceClass = GoodsService.class)

public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private SellerDao sellerDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemDao itemDao;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods,String Role) {
        PageHelper.startPage(page, rows);

        //Example设置条件，相当于where后的语句
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //获得当前操作的角色
        if("ROLE_USER".equals(Role)){
            //如果是运营商，不查询查询审核状态为0的
            criteria.andNotEqualTo("auditStatus","0");
        }else{
            //如果是商家，则正常查询
            if(!StringUtils.isEmpty(goods.getAuditStatus())){
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
            }
        }
        //商家限定（限定商家id，商品审核状态，商品名）
        //不查询已删除的商品
        criteria.andNotEqualTo("isDelete","1");
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andLike("sellerId", "%" + goods.getSellerId() + "%");
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsDao.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    @Transactional
    public void addGoods(Goods goods) {

        //新增商品基本信息
        goodsDao.insertSelective(goods.getGoods());
        //add(goods.getGoods());

        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescDao.insertSelective(goods.getGoodsDesc());

        //保存商品sku列表
        saveItemList(goods);

    }

    //回显商品
    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();

        //查询商品spu
        TbGoods tbGoods = goodsDao.selectByPrimaryKey( id);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        //查询商品SKU列表
        Example example = new Example(TbGoods.class);
        example.createCriteria().andEqualTo("id",id);
        List<TbItem> list = itemDao.selectByExample(example);
        goods.setItemList(list);

        return goods;
    }

    //更新商品
    @Override
    public void updateGoods(Goods goods) {
        //更新商品基本信息
        goods.getGoods().setAuditStatus("0");//修改过的商品状态为未审核
        goodsDao.updateByPrimaryKeySelective(goods.getGoods());

        //更新商品描述信息
        goodsDescDao.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //更新商品SKU
        //删除原SKU
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(goods.getGoods().getId());
        itemDao.delete(tbItem);

        //保存新的SKU
        saveItemList(goods);
    }

    //提交审核
    @Override
    public void updateStatus(Long[] ids, String status) {
        //设置商品审核状态
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        //添加操作对象（查询条件）
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        //批量更新商品审核状态
        goodsDao.updateByExampleSelective(goods,example);

        //如果审核通过
        if("2".equals(status)){
            //更新内容
            TbItem item = new TbItem();
            item.setStatus("1");

            //设置查询条件
            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId",Arrays.asList(ids));

            //执行更新
            itemDao.updateByExampleSelective(item,itemExample);
        }
    }

    private void saveItemList(Goods goods) {
        //判断是否启动规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            //启动规格，则按照规格生成不同的SKU商品
            for(TbItem item :goods.getItemList()){
                String title = goods.getGoods().getGoodsName();

                //组合规格选项形成SKU标题
                Map<String,Object> map = JSON.parseObject(item.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for(Map.Entry<String,Object> entity : entries){
                    title += " "+entity.getValue().toString();
                }
                item.setTitle(title);

                setItemValue(item,goods);

                itemDao.insertSelective(item);
            }
        }else{
            //没有启动，则只存一条来自SPU的SKU信息
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setNum(9999);
            tbItem.setStatus("0");
            tbItem.setIsDefault("1");
            tbItem.setSpec("{}");

            setItemValue(tbItem,goods);

            itemDao.insertSelective(tbItem);
        }

    }

    private void setItemValue(TbItem item, Goods goods) {
        //图片
        List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if(imgList != null && imgList.size() > 0){
            //将商品第一张图片作为sku图片
            item.setImage(imgList.get(0).get("url").toString());
        }
        //商品分类id：设置为3级分类的id
        item.setCategoryid(goods.getGoods().getCategory3Id());
        //商品分类名称：设置为3级分类的名称
        TbItemCat itemCat = itemCatDao.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());

        //创建时间
        item.setCreateTime(new Date());

        //更新时间
        item.setUpdateTime(item.getCreateTime());

        //SPU商品id
        item.setGoodsId(goods.getGoods().getId());

        //商家id
        item.setSellerId(goods.getGoods().getSellerId());

        //商家名称
        TbSeller seller = sellerDao.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());

        //品牌名称
        TbBrand brand = brandDao.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
    }

    //删除商品
    public void deleteGoodsByIds(Long[] ids){
        //根据spu id更新商品删除状态为已删除
        //update tb_goods set id_delete = 1 where id in

        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");

        //example用于添加条件，相当where后面的部分

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        goodsDao.updateByExampleSelective(goods,example);

        /*if (ids != null && ids.length > 0){
            for (Long id:ids){
                Example example = new Example(TbGoods.class);
                Example.Criteria criteria = example.createCriteria();

                criteria.andEqualTo("id",id);
                goodsDao.updateByExampleSelective(goods,example);
            }
        }*/

    }

    //商品上下架
    @Override
    public void isMarkeTable(Long[] ids, String status) {
        TbGoods goods = new TbGoods();

        //1就上架，0就下架
        goods.setIsMarketable(status);

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        goodsDao.updateByExampleSelective(goods,example);
    }

    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("status",status)
                .andIn("goodsId",Arrays.asList(ids));

        return itemDao.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String status) {
        Goods goods = new Goods();
        //查询sku
        TbGoods tbgoods = goodsDao.selectByPrimaryKey(goodsId);
        goods.setGoods(tbgoods);

        //查询商品面熟
        TbGoodsDesc tbgoodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbgoodsDesc);

        //查询商品sku列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("goodsId", goodsId);
        if(!StringUtils.isEmpty(status)){
            criteria.andEqualTo("status",status);
        }
        //按照是否默认值降序，默认值为，反则为0
        example.orderBy("isDefault").desc();

        List<TbItem> itemList = itemDao.selectByExample(example);
        goods.setItemList(itemList);

        return goods;
    }
}
