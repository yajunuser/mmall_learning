package com.mymmall.controller.portal;

import com.mymmall.common.ServerResponse;
import com.mymmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 商品详情页
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(Integer productId) {
       return  productService.detail(productId);
    }
    /**
     * 动态的排序列表，根据用户的搜索
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyWord",required = false) String keyWord,
                               @RequestParam(value = "categoryId",required = false)Integer categoryId,
                               @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                               @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        return productService.list(keyWord, categoryId, pageNum, pageSize, orderBy);
    }
}
