package com.mymmall.controller.backend;

import com.mymmall.common.Const;
import com.mymmall.common.ServerResponse;
import com.mymmall.pojo.User;
import com.mymmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private CategoryService categoryService;
/**
 * 后台的产品分类管理模块
 */
    /**
     * 通过管理员添加商品
     *
     * @param session      判断管理用户是否登录了
     * @param parentId     商品的节点id
     * @param categoryName 商品的名字
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    public ServerResponse addCategory(HttpSession session, Integer parentId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
            //说明是管理员 开始操作数据
            return categoryService.addCategory(parentId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员身份");
        }
    }

    /**
     * @param session      用来判断用户是否登录 并且是管理员
     * @param categoryId   通过商品的id 查找该商品
     * @param categoryName 新名字
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update_category.do", method = RequestMethod.POST)
    public ServerResponse updateCategory(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
            //说明是管理员 开始操作数
            return categoryService.updateCategory(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员身份");
        }
    }

    /**
     * 获得平行类别的商品 就是获得平行节点的商品数据
     */
    @ResponseBody
    @RequestMapping(value = "get_children_parallel_category.do", method = RequestMethod.POST)
    public ServerResponse getChildrenParallelCategory(HttpSession session, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
            //说明是管理员 开始查询商品信息
            return categoryService.getParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员身份");
        }
    }
    /**
     * 查找某节点下的所有递归商品
     */
    @ResponseBody
    @RequestMapping(value = "select_category_and_children_by_id.do", method = RequestMethod.POST)
    public ServerResponse selectCategoryAndChildrenById(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        if (user.getRole().equals(Const.Role.ROLE_ADMIN)) {
            //说明是管理员 开始查询商品信息
            return categoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，需要管理员身份");
        }
    }
}
