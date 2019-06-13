package com.mymmall.service.Impl;

import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.ProductMapper;
import com.mymmall.pojo.Product;
import com.mymmall.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 后台保存商品的业务
     */
    @Override
    public ServerResponse saveAndUpdateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品的参数错误");
        }
        //还要判断有没有图片 把主图设置上去
        if (StringUtils.isNotBlank(product.getSubImages())) {
            //不是空的话就把第一张图片 设置为主图
            String[] subimagesArray = product.getSubImages().split(",");
            //把第一张图放到主图上
            if (subimagesArray.length > 0) {
                product.setMainImage(subimagesArray[0]);
            }
        }
        //判断是添加商品 还是更新商品
        if (product.getId()==null) {
           int resultCount =  productMapper.insert(product);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("添加商品成功");
            }
            return ServerResponse.createBySuccessMessage("添加商品失败");
        }else{
            int resultCount = productMapper.updateByPrimaryKey(product);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("更新商品成功");
            }
            return ServerResponse.createBySuccessMessage("更新商品失败");
        }
    }

    /**
     * 修改商品销售状态 上下架
     */
    @Override
    public ServerResponse updateStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //创建一个新的对象 作为跟新的载体，不需要做多余的判断
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("更新状态成功");
        }
        return ServerResponse.createByErrorMessage("更新状态失败");
    }
}
