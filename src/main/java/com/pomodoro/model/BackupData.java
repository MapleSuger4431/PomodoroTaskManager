package com.pomodoro.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 备份数据实体类，用于一次性序列化导出系统全部数据
 */
public class BackupData implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 全部用户数据 */
    private List<User> users;

    /** 全部任务数据 */
    private List<Task> tasks;

    /** 全部番茄钟记录数据 */
    private List<PomodoroRecord> records;

    /** 全部每日任务记录数据 */
    private List<TaskDailyRecord> dailyRecords;

    /**
     * 无参构造方法，并初始化各个集合
     */
    public BackupData() {
        this.users = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.records = new ArrayList<>();
        this.dailyRecords = new ArrayList<>();
    }

    /**
     * 获取全部用户数据
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * 设置全部用户数据
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * 获取全部任务数据
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * 设置全部任务数据
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * 获取全部番茄钟记录数据
     */
    public List<PomodoroRecord> getRecords() {
        return records;
    }

    /**
     * 设置全部番茄钟记录数据
     */
    public void setRecords(List<PomodoroRecord> records) {
        this.records = records;
    }

    /**
     * 获取全部每日任务记录数据
     */
    public List<TaskDailyRecord> getDailyRecords() {
        return dailyRecords;
    }

    /**
     * 设置全部每日任务记录数据
     */
    public void setDailyRecords(List<TaskDailyRecord> dailyRecords) {
        this.dailyRecords = dailyRecords;
    }
}
