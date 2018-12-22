package vo;

import entity.TbSpecification;
import entity.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/*
    配置规格和规格选项一对多的关系
 */
public class Specification implements Serializable{

    private TbSpecification specification;
    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
