package com.pomodoro.ui;

/**
 * 任务管理菜单类，负责任务的增删改查交互（骨架，具体实现待补充）
 */
public class TaskMenu {

    /**
     * 展示任务管理菜单
     */
    public void show(String userId) {
        // TODO 展示任务管理菜单并循环处理用户选择
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 展示当前用户的任务列表
     */
    private void handleList() {
        // TODO 查询并展示当前用户的任务列表
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 处理创建任务
     */
    private void handleCreate() {
        // TODO 读取任务信息并调用 TaskService 创建新任务
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 处理更新任务
     */
    private void handleUpdate() {
        // TODO 选择任务并读取修改内容后更新任务
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 处理删除任务
     */
    private void handleDelete() {
        // TODO 选择任务并调用 TaskService 逻辑删除
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 处理标记任务完成
     */
    private void handleMarkDone() {
        // TODO 选择任务并调用 TaskService 标记为已完成
        throw new UnsupportedOperationException("待实现");
    }
}
