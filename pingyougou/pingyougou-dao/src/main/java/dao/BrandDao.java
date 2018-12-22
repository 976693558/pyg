package dao;

import entity.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandDao extends Mapper<TbBrand> {

    //查询全品牌
    List<TbBrand> findAllBrand();

    List<Map<String,Object>> selectOptionList();

}
