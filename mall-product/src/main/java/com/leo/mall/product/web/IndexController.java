package com.leo.mall.product.web;

import com.leo.mall.product.entity.CategoryEntity;
import com.leo.mall.product.service.CategoryService;
import com.leo.mall.product.vo.Catelog2VO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryList = categoryService.getLeve1Categorys();
        model.addAttribute("categorys",categoryList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2VO>> getCatalogJson() {
        Map<String, List<Catelog2VO>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务");
        } finally {
            lock.unlock();
        }

        return "hello";
    }

}
