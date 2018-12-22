package search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import search.service.ItemSearchService;

import java.util.Map;

@RequestMapping("/itemSearch")
@RestController
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    //根据搜索关键字搜索商品列表
    @PostMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){
        return itemSearchService.search(searchMap);
    }
}
