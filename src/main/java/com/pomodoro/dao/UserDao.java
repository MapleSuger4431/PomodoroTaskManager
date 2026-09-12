package com.pomodoro.dao;

import com.pomodoro.model.User;

/**
 * 用户数据访问接口，定义用户表的专属查询操作
 */
public interface UserDao extends BaseDao<User> {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
}
