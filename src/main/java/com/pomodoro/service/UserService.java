package com.pomodoro.service;

import com.pomodoro.dao.UserDao;
import com.pomodoro.dao.impl.UserDaoImpl;
import com.pomodoro.model.User;
import com.pomodoro.util.HashUtil;

/**
 * 用户业务逻辑类，负责用户注册、登录与会话状态管理
 */
public class UserService {

    /** 用户数据访问对象 */
    private UserDao userDao = new UserDaoImpl();

    /** 当前登录的用户 */
    private User currentUser;

    /**
     * 注册新用户，用户名重复或输入为空时抛出 IllegalArgumentException
     */
    public User register(String username, String password) {
        // 校验用户名和密码不能为空
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        // 校验用户名是否已被注册
        if (userDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 密码经 SHA-256 哈希后保存
        User user = new User();
        user.setUsername(username);
        user.setPassword(HashUtil.sha256(password));
        userDao.insert(user);
        return user;
    }

    /**
     * 用户登录，用户名不存在或密码错误时抛出 IllegalArgumentException
     */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        // 用户不存在时与密码错误保持相同提示，避免暴露用户名是否存在
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        // 比对输入密码的哈希值与数据库中存储的哈希值
        String hashed = HashUtil.sha256(password);
        if (hashed == null || !hashed.equals(user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        this.currentUser = user;
        return user;
    }

    /**
     * 获取当前登录的用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 退出登录，清空当前登录用户
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * 判断是否已有用户登录
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
