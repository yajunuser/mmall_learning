package com.mymmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.dao.CategoryMapper;
import com.mymmall.dao.ProductMapper;
import com.mymmall.pojo.Category;
import com.mymmall.pojo.Product;
import com.mymmall.service.CategoryService;
import com.mymmall.service.ProductService;
import com.mymmall.util.DateTimeUtil;
import com.mymmall.util.PropertiesUtil;
import com.mymmall.vo.ProductDataVo;
import com.mymmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService  categoryService;

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
        if (product.getId() == null) {
            int resultCount = productMapper.insert(product);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("添加商品成功");
            }
            return ServerResponse.createBySuccessMessage("添加商品失败");
        } else {
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

    /**
     * 得到商品的详细信息 通过商品的id
     */
    public ServerResponse getProductDetail(Integer productId) {
        if (productId == null) {
            //返回错误信息 说产品的参数错误
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //看看有没有这个商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在，删除或下架");
        }
        //走到这一步说明商品存在，返回商品信息，由于之间面像门户需要对对象进行处理，信息不需要全部显示需要显示部分信息
        //这里创建一个vo对象 把vo对象返回给前端
        ProductDataVo productDataVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDataVo);
    }

    //这里创建一个封装vo对象的方法 设置成私有方法
    private ProductDataVo assembleProductDetailVo(Product product) {
        ProductDataVo productDataVo = new ProductDataVo();
        //把
        productDataVo.setId(product.getId());
        productDataVo.setSubtitle(product.getSubtitle());
        productDataVo.setPrice(product.getPrice());
        productDataVo.setMainImage(product.getMainImage());
        productDataVo.setSubImage(product.getSubImages());
        productDataVo.setCategoryId(product.getCategoryId());
        productDataVo.setDetail(product.getDetail());
        productDataVo.setName(product.getName());
        productDataVo.setStatus(product.getStatus());
        productDataVo.setStock(product.getStock());

        //设置ImageHost 这个暂时不知道什么用
        productDataVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        //设值商品的parentCategoryId
        //一定看看是根节点 还是子节点 先用商品的categoryId 商品分类和中找找  有的话就是一个根节点
        //没有就是个根节点的子节点 或者子节点的子节点
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDataVo.setParentCategoryId(0);
        } else {
            productDataVo.setParentCategoryId(category.getParentId());
        }
        productDataVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDataVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDataVo;
    }

    /**
     * 获得商品的展示信息 分页插件
     */
    public ServerResponse getProductDetailList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //获取所有的时间
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product p : productList) {
            ProductListVo productListVo = assembleProductListVo(p);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //把product 转换成vo
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    /**
     * 商品搜索
     */

    public ServerResponse searchProductList(String productName, Integer productId, int pageNum, int pageSize) {
        //判断名字商品的id先
        if (StringUtils.isBlank(productName) && productId == null) {
            return ServerResponse.createByErrorMessage("参数不能为空");
        }
        if (StringUtils.isNotBlank(productName)) {
            //FIXME stringbuffer与stringbuilder的区别 前者线程安全效率低，后者线程不安全效率高 不考虑安全问题用builder！！
            productName = new StringBuilder("%").append(productName).append("%").toString();
        }
        //写一个模糊查询的sql
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectSearchProductBy(StringUtils.isBlank(productName)?null:productName, productId);
        //这里还需要把数据包装成vo 然后排序
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product p : productList) {
            ProductListVo productListVo = assembleProductListVo(p);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 获得商品详情的业务
     */
    @Override
    public ServerResponse detail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在");
        }
        //FIXME 在这里Status的状态 用数字1判断有点娄，所以我们每进一个常量；Const.productStatus.ON_LINE.getCode() == 1
        if (product.getStatus() != Const.productStatus.ON_LINE.getCode()) {
            return ServerResponse.createByErrorMessage("商品已下架，或删除");
        }
        //把商品转成显示的vo对象
        ProductDataVo productDataVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDataVo);
    }

    /**
     * 动态的排序列表 带上产品搜索
     */
    @Override
    public ServerResponse list(String keyWord, Integer categoryId, int pageNum, int pageSize,String orderBy) {
        if (StringUtils.isBlank(keyWord) && categoryId == null) {
            //提示商品的信息有误
            return ServerResponse.createByCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyWord)) {
                //这是分类的id  没查到说明没有此分类 返回一个集合
                PageHelper.startPage(pageNum, pageSize);
                List<Category> categoryList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(categoryList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //泛型用的是Object  强转吧
            categoryIdList = (List<Integer>) categoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyWord)) {
            keyWord =new StringBuilder("%").append(keyWord).append("%").toString();
        }
        //FIXME 逻辑有点乱，意思是（1）如果商品的分类id不是空，用此id去分类中找如果是空就是说明不存在此分类，此时商品的关键词
        //FIXME 也是空就返回一个空结果集，（2）不是空的话 就找出此分类的所有子分类的id
        //FIXME 如果关键词不是空那就把关键词，拼接成符合sql语句的格式 通过关键词 查找与id是或者此商品id子分类id符合的商品
        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        //还有一个Orderby需要处理
        if (StringUtils.isNotBlank(orderBy)) {
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        //写一个Sql语句实现模糊查找
        List<Product> productList = productMapper.selectSearchProductByKeyWordAndList(StringUtils.isBlank(keyWord)?null:keyWord, categoryIdList.size()== 0?null:categoryIdList);
        //貌似需要把这个转成vo
        List<ProductDataVo> productDataVoList = Lists.newArrayList();
        for (Product p : productList) {
            ProductDataVo productDataVo = assembleProductDetailVo(p);
            productDataVoList.add(productDataVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productDataVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
