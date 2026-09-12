package com.pomodoro.ui;

import com.pomodoro.service.StatsService;
import com.pomodoro.service.TaskService;
import com.pomodoro.util.ConsoleUtil;
import com.pomodoro.util.DateUtil;

/**
 * 统计信息菜单类，负责展示用户的专注数据统计
 */
public class StatsMenu {

    /** 统计业务逻辑对象 */
    private StatsService statsService = new StatsService();

    /** 任务业务逻辑对象，用于展示前刷新每日记录 */
    private TaskService taskService = new TaskService();

    /**
     * 展示统计信息菜单
     */
    public void show(String userId) {
        try {
            // 展示前先刷新每日记录，保证逾期标记与统计数据是最新的
            taskService.refreshDailyRecords(userId);
        } catch (Exception e) {
            // 刷新失败不阻断统计展示
            System.out.println("提示：刷新每日记录失败，统计基于当前数据展示");
        }
        System.out.println("========== 统计 ==========");
        System.out.println("总任务完成次数：" + statsService.getTotalCompletedCount(userId));
        System.out.println("本周任务完成次数：" + statsService.getWeeklyCompletedCount(userId));
        System.out.println("总专注时长：" + DateUtil.formatDuration(statsService.getTotalFocusDuration(userId)));
        System.out.println("本周专注时长：" + DateUtil.formatDuration(statsService.getWeeklyFocusDuration(userId)));
        System.out.println("未完成次数：" + statsService.getUnfinishedCount(userId));
        System.out.println("============================");
        ConsoleUtil.pressEnterToContinue();
    }
}
