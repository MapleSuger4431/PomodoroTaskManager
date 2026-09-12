package com.pomodoro.service;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;

/**
 * 统计业务逻辑类，负责番茄完成数与专注时长的汇总统计（骨架，具体实现待补充）
 */
public class StatsService {

    /** 任务数据访问对象 */
    private TaskDao taskDao;

    /** 番茄钟记录数据访问对象 */
    private PomodoroRecordDao recordDao;

    /** 每日任务记录数据访问对象 */
    private TaskDailyRecordDao dailyDao;

    /**
     * 统计用户累计完成的番茄总数
     */
    public int getTotalCompletedCount(String userId) {
        // TODO 汇总指定用户全部已完成的番茄数量
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 统计用户本周完成的番茄数
     */
    public int getWeeklyCompletedCount(String userId) {
        // TODO 汇总指定用户本周（周一至周日）完成的番茄数量
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 统计用户累计专注总时长
     */
    public int getTotalFocusDuration(String userId) {
        // TODO 汇总指定用户全部番茄记录的有效时长（秒）
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 统计用户本周专注时长
     */
    public int getWeeklyFocusDuration(String userId) {
        // TODO 汇总指定用户本周（周一至周日）番茄记录的有效时长（秒）
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 统计用户未完成的任务数
     */
    public int getUnfinishedCount(String userId) {
        // TODO 统计指定用户处于待完成或逾期状态的任务数量
        throw new UnsupportedOperationException("待实现");
    }
}
