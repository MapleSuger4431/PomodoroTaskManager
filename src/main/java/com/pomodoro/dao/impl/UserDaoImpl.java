package com.pomodoro.dao.impl;

import com.pomodoro.dao.UserDao;
import com.pomodoro.model.User;

import java.util.List;

/**
 * 用户数据访问实现类，负责 users 表的增删改查（骨架，具体实现待补充）
 */
public class UserDaoImpl implements UserDao {

    @Override
    public void insert(User entity) {
        // TODO 向 users 表插入一条用户记录
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public void update(User entity) {
        // TODO 根据 id 更新 users 表中的用户信息
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public void deleteById(String id) {
        // TODO 从 users 表中删除指定 id 的用户
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public User findById(String id) {
        // TODO 查询 users 表，按 id 返回 User
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public List<User> findAll() {
        // TODO 查询 users 表，返回全部用户列表
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public void deleteAll() {
        // TODO 清空 users 表中的全部用户数据
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public synchronized String generateId() {
        // TODO 查询 users 表 MAX(id)，生成 U001 格式 ID
        throw new UnsupportedOperationException("待实现");
    }

    @Override
    public User findByUsername(String username) {
        // TODO 查询 users 表，按用户名返回 User
        throw new UnsupportedOperationException("待实现");
    }
}
