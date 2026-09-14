package com.pomodoro.ui;

import com.pomodoro.model.Task;
import com.pomodoro.service.TaskService;
import com.pomodoro.service.TimerService;
import com.pomodoro.util.ConsoleUtil;

import java.util.List;

/**
 * 番茄计时菜单
 */
public class TimerMenu {

    private TimerService timerService = new TimerService();
    private TaskService taskService = new TaskService();

    public void show(String userId) {
        while (true) {
            System.out.println("========== 番茄计时 ==========");
            List<Task> tasks = taskService.listTimerSelectableTasks(userId);

            if (tasks.isEmpty()) {
                System.out.println("暂无可用任务，请先在任务管理中添加");
                System.out.println("0. 返回");
                ConsoleUtil.pressEnterToContinue();
                return;
            }

            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i).getId() + " " + tasks.get(i).getTitle());
            }
            System.out.println("0. 返回");
            int choice = ConsoleUtil.readInt("请选择要计时的任务：");

            if (choice == 0) {
                return;
            }

            if (choice >= 1 && choice <= tasks.size()) {
                Task task = tasks.get(choice - 1);
                handleStartTimer(task);
            } else {
                System.out.println("无效选择");
                ConsoleUtil.pressEnterToContinue();
            }
        }
    }

    /**
     * 启动指定任务的计时
     */
    private void handleStartTimer(Task task) {
        timerService.startSession(task);
        ConsoleUtil.pressEnterToContinue();
    }
}
