package com.mymmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USSERNAME = "username";
    public static final String EMAIL = "email";
    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN =1 ;//管理员用户
    }
    public  interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
    public interface Cart{
        //购物车选中状态
        int CHECKED = 1;
        //购物车未选中状态
        int UN_CHECKED= 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

    }


    public enum productStatus{
        ON_LINE(1,"在线");
        private final Integer code;
        private final String msg;

        productStatus(Integer code, String msg) {
            this.code=code;
            this.msg = msg;
        }
        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
