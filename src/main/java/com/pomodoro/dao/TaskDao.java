package com.pomodoro.dao;

import com.pomodoro.model.Task;

import java.util.List;

/**
 * 任务数据访问接口，定义任务表的专属操作
 */
public interface TaskDao extends BaseDao<Task> {

    /**
     * 查询指定用户的全部任务
     */
    List<Task> findByUserId(String userId);

    /**
     * 查询指定用户未被逻辑删除的任务
     */
    List<Task> findNotDeletedByUserId(String userId);

    /**
     * 查询指定用户按日期重复的任务
     */
    List<Task> findRepeatDateTasksByUser(String userId);

    /**
     * 逻辑删除指定任务
     */
    void logicDelete(String taskId);

    /**
     * 更新指定任务的已完成次数
     */
    void updateCompletedCount(String taskId, int count);

    /**
     * 将指定任务标记为已完成
     */
    void markDone(String taskId);

    /**
     * 插入一条使用指定 ID 的任务记录
     */
    void insertWithId(Task task);
}
