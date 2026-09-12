package com.pomodoro.dao;

import java.util.List;

/**
 * 通用数据访问基础接口，定义对单张表的通用增删改查操作
 *
 * @param <T> 实体类型
 */
public interface BaseDao<T> {

    /**
     * 插入一条实体记录
     */
    void insert(T entity);

    /**
     * 根据实体 ID 更新记录
     */
    void update(T entity);

    /**
     * 根据 ID 删除实体记录
     */
    void deleteById(String id);

    /**
     * 根据 ID 查询单个实体
     */
    T findById(String id);

    /**
     * 查询全部实体列表
     */
    List<T> findAll();

    /**
     * 删除全部实体记录
     */
    void deleteAll();

    /**
     * 生成下一个可用的实体 ID
     */
    String generateId();
}
