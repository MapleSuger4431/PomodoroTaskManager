package com.pomodoro.service;

import com.pomodoro.dao.UserDao;
import com.pomodoro.dao.impl.UserDaoImpl;
import com.pomodoro.model.User;

/**
 * 用户业务逻辑类，负责用户注册、登录与会话状态管理（骨架，具体实现待补充）
 */
public class UserService {

    /** 用户数据访问对象 */
    private UserDao userDao = new UserDaoImpl();

    /** 当前登录的用户 */
    private User currentUser;

    /**
     * 注册新用户
     */
    public User register(String username, String password) {
        // TODO 校验用户名唯一性，密码哈希后保存并返回新用户
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        // TODO 校验用户名和密码，成功后记录当前用户并返回
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 获取当前登录的用户
     */
    public User getCurrentUser() {
        // TODO 返回当前登录的用户
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 退出登录
     */
    public void logout() {
        // TODO 清空当前登录用户
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 判断是否已有用户登录
     */
    public boolean isLoggedIn() {
        // TODO 判断当前是否有用户处于登录状态
        throw new UnsupportedOperationException("待实现");
    }
}
