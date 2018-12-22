package shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbItemCat;
import org.springframework.web.bind.annotation.*;
import sellergoods.service.ItemCatService;
import vo.PageResult;
import vo.Result;

import java.util.List;

@RequestMapping("/itemCat")
@RestController
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() {
        return itemCatService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return itemCatService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.add(itemCat);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbItemCat findOne(Long id) {
        return itemCatService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            itemCatService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param itemCat 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbItemCat itemCat, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return itemCatService.search(page, rows, itemCat);
    }

    //查找父id
    @GetMapping("findByParentId")
    public List<TbItemCat> findByParentId(Long parentId){
        TbItemCat itemCat = new TbItemCat();
        itemCat.setParentId(parentId);
        return itemCatService.findByWhere(itemCat);
    }
}
