package content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import content.service.ContentService;
import dao.ContentDao;
import dao.impl.BaseServiceImpl;
import entity.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //在redis中内容对应的key
    private static final String REDIS_CONTENT = "content";

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentDao.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    //轮播图：查询符合要求的数据
    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> list= null;
        //先从redis中查找
        try{
            list = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);
            if(list != null){
                return list;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria().andEqualTo("categoryId", categoryId);
        //启用状态的
        criteria.andEqualTo("status","1");
        //降序排序
        example.orderBy("sortOrder").desc();
        list = contentDao.selectByExample(example);

        //如果是从mysql数据库中获取的，将其保存到redis中
        try{
            redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,list);
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void add(TbContent tbContent){
        super.add(tbContent);
        //更新内容分类对应在redis中的内容列表缓存
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    //通过分类id删除旧的数据
    private void updateContentInRedisByCategoryId(Long categoryId) {
        try{
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(TbContent tbContent){
        //通过id查询旧的分类值
        TbContent oldContent = super.findOne(tbContent.getId());

        //更新新的分类值
        super.update(tbContent);

        //是否修改了内容分类，如果修改了则需要将新旧分类对应的内容列表都更新
        if(!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids){
        //根据内容id集合查询内容列表，然后更新该内容分类对应的内容列表缓存
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        List<TbContent> list = contentDao.selectByExample(example);
        if(list != null && list.size() > 0){
            for (TbContent content : list){
                updateContentInRedisByCategoryId(content.getCategoryId());
            }
        }

        //删除内容
        super.deleteByIds(ids);
    }
}
