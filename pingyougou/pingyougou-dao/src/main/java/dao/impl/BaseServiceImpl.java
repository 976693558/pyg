package dao.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import service.BaseService;
import tk.mybatis.mapper.common.Mapper;
import vo.PageResult;

import java.io.Serializable;
import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {

    //这个@Autowired是spring4.0之后增强的
    @Autowired
    private Mapper<T> mapper;

    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize, T t) {
        PageHelper.startPage(pageNum,pageSize);
        List<T> list = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void add(T t) {
        mapper.insertSelective(t);
    }

    @Override
    public void update(T t) {
        mapper.updateByPrimaryKeySelective(t);
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        if(ids != null && ids.length > 0){
            for (Serializable id : ids){
                mapper.deleteByPrimaryKey(id);
            }
        }
    }
}
