package dao;


import entity.TbSpecification;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecificationDao extends Mapper<TbSpecification> {

    List<Map<String,Object>> selectOptionList();
}
