package solr;

import com.alibaba.fastjson.JSON;
import dao.ItemDao;
import entity.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/applicationContext-solr.xml")
public class ItemImport2SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    @Test
    public void test(){
        //获取已经通过审核商品列表
        TbItem param = new TbItem();
        param.setStatus("1");
        List<TbItem> itemList = itemDao.select(param);

        //转换商品规格
        for(TbItem item : itemList){
            Map specMap = JSON.parseObject(item.getSpec(),Map.class);
            item.setSpecMap(specMap);
        }

        //导入商品列表
        solrTemplate.saveBean(itemList);
        solrTemplate.commit();
    }
}
