package com.pomodoro.service;

import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.model.Task;

import java.util.List;

/**
 * 任务业务逻辑类，负责任务的增删改查与每日记录刷新（骨架，具体实现待补充）
 */
public class TaskService {

    /** 任务数据访问对象 */
    private TaskDao taskDao;

    /** 每日任务记录数据访问对象 */
    private TaskDailyRecordDao dailyDao;

    /**
     * 创建新任务
     */
    public void createTask(Task task) {
        // TODO 生成任务 ID 并保存新任务
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 更新任务信息
     */
    public void updateTask(Task task) {
        // TODO 更新指定任务的详细信息
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 删除任务
     */
    public void deleteTask(String taskId) {
        // TODO 逻辑删除指定任务
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 将任务标记为已完成
     */
    public void markDone(String taskId) {
        // TODO 将指定任务状态置为已完成
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 查询用户的任务列表
     */
    public List<Task> listTasks(String userId) {
        // TODO 返回指定用户未被逻辑删除的任务列表
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 刷新用户的每日任务记录
     */
    public void refreshDailyRecords(String userId) {
        // TODO 为指定用户按日期重复的任务生成或刷新当天的每日记录
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 查询可用于番茄钟计时的任务列表
     */
    public List<Task> listTimerSelectableTasks(String userId) {
        // TODO 返回指定用户当前可进行计时的任务列表
        throw new UnsupportedOperationException("待实现");
    }
}
