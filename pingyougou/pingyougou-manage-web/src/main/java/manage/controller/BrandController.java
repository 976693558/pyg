package manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbBrand;
import org.springframework.web.bind.annotation.*;
import sellergoods.service.BrandService;
import vo.PageResult;
import vo.Result;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController
public class BrandController {

    //@Reference表示该用户（变量）就是消费者（在springMVC中有详细配置）
    @Reference
    private BrandService bs;

    @GetMapping("/findAll")
    public List<TbBrand> queryAll(){
        System.out.println("-------------------------------------------------");
        /*List<TbBrand> list = bs.findAllBrand();
        System.out.println(list);
        return list;*/
        return bs.findAll();
    }

    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize){
        //常规路径
        //return bs.testPage(pageNum,pageSize);

        //通用mapper路径
        return (List<TbBrand>) bs.findPage(pageNum,pageSize);
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1")Integer pageNum,
                                @RequestParam(value = "rows",defaultValue = "10")Integer pageSize){
        return bs.findPage(pageNum,pageSize);
    }

    //添加
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try{
            bs.add(brand);
            return Result.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("添加失败");
    }

    //修改
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return bs.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try{
            bs.update(brand);
            return Result.ok("添加成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("添加失败");
    }

    //批量删除
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        System.out.println("delete  ================================================================ "+ids);
        try{
            bs.deleteByIds(ids);
            return Result.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("删除成功");
    }

    //条件查询
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(value = "page",defaultValue = "1")Integer pageNum,
                             @RequestParam(value = "rows",defaultValue = "10")Integer pageSize){
        return bs.search(brand,pageNum,pageSize);
    }

    //查询品牌列表，返回的数据格式符合select2格式
    @GetMapping("/selectOptionList")
    public List<Map<String,Object>> selectOptionList(){
        return bs.selectOptionList();
    }

}
