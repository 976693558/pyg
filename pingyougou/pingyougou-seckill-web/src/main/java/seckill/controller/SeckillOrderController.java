package seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbSeckillOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import seckill.service.SeckillOrderService;
import vo.PageResult;
import vo.Result;

import java.util.List;

@RequestMapping("/seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillOrderService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillOrderService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seckillOrder 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeckillOrder seckillOrder, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillOrderService.search(page, rows, seckillOrder);
    }

    //根据秒杀商品号创建订单
    @GetMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        try{
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println();
            System.out.println("username ============== "+username);
            System.out.println();
            //判断登陆
            if(!"anonymousUser".equals(username)){
                //查询订单
                String orderId = seckillOrderService.submitOrder(username,seckillId);
                if(orderId != null){
                    //返回订单
                    return Result.ok(orderId);
                }
            }else{
                return Result.fail("请先登录！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.fail("提交订单失败！");
    }

}
