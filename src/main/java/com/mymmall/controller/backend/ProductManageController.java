package com.mymmall.controller.backend;

import com.google.common.collect.Maps;
import com.mymmall.common.Const;
import com.mymmall.common.ResponseCode;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.Product;
import com.mymmall.pojo.User;
import com.mymmall.service.FileService;
import com.mymmall.service.IUserService;
import com.mymmall.service.ProductService;
import com.mymmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台的商品管理模块
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    /**
     * 后台保存商品的接口
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.saveAndUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 修改商品的销售状态
     */
    @RequestMapping("update_status.do")
    @ResponseBody
    public ServerResponse updateStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.updateStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 获取商品详情信息的接口
     */
    @RequestMapping("get_product_detail.do")
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId) {
        //判断用户是否登录 还有身份信息
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.getProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }
    /**
     * list 这里需要用到分页 所以要传入两个参数int 一个是目前的页数 一个是一个页数显示多少信息
     * 设置默认值 页数起始是第一页 一页显示10条数据
     */
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //判断用户是否登录 还有身份信息
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.getProductDetailList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 产品搜索 商品搜索
     *///这是一个简单的模糊查询 有商品的关键字，和id
    @RequestMapping("search_product_list.do")
    @ResponseBody
    public ServerResponse searchProductList(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //先判断管理是不是已经登录了
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            return productService.searchProductList(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 图片上传
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request) {
        //判断操作者
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //在这里需要强制登录下
            return ServerResponse.createByCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要强制登录");
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            //说明是管理员 可以做对应的操作
            //先获取request中的地址 也就是tomcat中  severlet容器的地址 就是上传到这个地址
            String path = request.getSession().getServletContext().getRealPath("upload");
            String fileName = fileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+fileName;
            Map fileMap = new HashMap();
            fileMap.put("uri", fileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("权限不够，需要管理员身份");
        }
    }

    /**
     * 富文本上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("rich_text_upload.do")
    @ResponseBody
    public Map richTextUpload(HttpSession session, @RequestParam(value = "file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","用户需要前置登录");
            return resultMap;
        }
        if (iUserService.isAdmin(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String fileName = fileService.upload(file, path);
            if (StringUtils.isBlank(fileName)) {
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+fileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("url", url);

            response.addHeader("Access-Controller-Allow-Headers", "X-File-Name");
            return  resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","权限不够，需要管理员身份");
            return resultMap;
        }
    }
}

