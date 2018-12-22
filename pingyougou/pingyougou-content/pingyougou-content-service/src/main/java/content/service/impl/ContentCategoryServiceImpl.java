package content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import content.service.ContentCategoryService;
import dao.ContentCategoryDao;
import dao.impl.BaseServiceImpl;
import entity.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import vo.PageResult;

import java.util.List;

@Service(interfaceClass = ContentCategoryService.class)
public class ContentCategoryServiceImpl extends BaseServiceImpl<TbContentCategory> implements ContentCategoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;

    @Override
    public PageResult search(Integer page, Integer rows, TbContentCategory contentCategory) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContentCategory.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(contentCategory.getName())){
            criteria.andLike("name", "%" + contentCategory.getName() + "%");
        }

        List<TbContentCategory> list = contentCategoryDao.selectByExample(example);
        PageInfo<TbContentCategory> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

}
