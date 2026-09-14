package com.pomodoro.ui;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.dao.impl.PomodoroRecordDaoImpl;
import com.pomodoro.dao.impl.TaskDaoImpl;
import com.pomodoro.dao.impl.TaskDailyRecordDaoImpl;
import com.pomodoro.model.PomodoroRecord;
import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.service.TaskService;
import com.pomodoro.util.ConsoleUtil;
import com.pomodoro.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录查询菜单
 */
public class RecordMenu {

    private PomodoroRecordDao recordDao = new PomodoroRecordDaoImpl();
    private TaskDao taskDao = new TaskDaoImpl();
    private TaskDailyRecordDao dailyDao = new TaskDailyRecordDaoImpl();
    private TaskService taskService = new TaskService();

    public void show(String userId) {
        while (true) {
            System.out.println("========== 记录查询 ==========");
            System.out.println("1. 按日期查询番茄记录");
            System.out.println("2. 按任务查询番茄记录");
            System.out.println("3. 按标签查询番茄记录");
            System.out.println("4. 查看全部番茄记录");
            System.out.println("5. 查询未完成任务");
            System.out.println("6. 查询未完成天数");
            System.out.println("0. 返回");
            int choice = ConsoleUtil.readInt("请选择：");

            if (choice == 0) {
                return;
            }

            if (choice == 1) {
                handleByDate(userId);
            } else if (choice == 2) {
                handleByTask(userId);
            } else if (choice == 3) {
                handleByTag(userId);
            } else if (choice == 4) {
                handleAll(userId);
            } else if (choice == 5) {
                handleUnfinishedTasks(userId);
            } else if (choice == 6) {
                handleUnfinishedDays(userId);
            } else {
                System.out.println("无效选择");
            }
            ConsoleUtil.pressEnterToContinue();
        }
    }

    /**
     * 按日期查询
     */
    private void handleByDate(String userId) {
        String dateStr = ConsoleUtil.readLine("请输入日期（yyyy-MM-dd）：");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("日期格式错误，请重新输入");
            return;
        }
        List<PomodoroRecord> records = recordDao.findByCondition(userId, date, null, null);
        printRecordList(records);
    }

    /**
     * 按任务查询
     */
    private void handleByTask(String userId) {
        List<Task> tasks = taskDao.findNotDeletedByUserId(userId);
        if (tasks.isEmpty()) {
            System.out.println("暂无任务记录");
            return;
        }
        System.out.println("任务列表：");
        for (Task task : tasks) {
            System.out.println(task.getId() + " - " + task.getTitle());
        }
        String taskId = ConsoleUtil.readLine("请输入任务ID：");
        List<PomodoroRecord> records = recordDao.findByCondition(userId, null, taskId, null);
        printRecordList(records);
    }

    /**
     * 按标签查询
     */
    private void handleByTag(String userId) {
        String tag = ConsoleUtil.readLine("请输入标签关键词：");
        List<PomodoroRecord> records = recordDao.findByCondition(userId, null, null, tag);
        printRecordList(records);
    }

    /**
     * 查询全部记录
     */
    private void handleAll(String userId) {
        List<PomodoroRecord> records = recordDao.findByUserId(userId);
        printRecordList(records);
    }

    /**
     * 查询未完成任务
     */
    private void handleUnfinishedTasks(String userId) {
        List<Task> tasks = taskDao.findNotDeletedByUserId(userId);
        System.out.println("========== 未完成任务 ==========");
        int count = 0;
        for (Task task : tasks) {
            boolean unfinished = false;
            if (task.getType() == TaskType.REPEAT_COUNT) {
                if (task.getCompletedCount() < task.getTargetCount() && task.getStatus() != TaskStatus.DONE) {
                    unfinished = true;
                }
            } else {
                if (task.getStatus() == TaskStatus.TODO || task.getStatus() == TaskStatus.OVERDUE) {
                    unfinished = true;
                }
            }
            if (unfinished) {
                System.out.println(task.getId() + " - " + task.getTitle());
                count++;
            }
        }
        if (count == 0) {
            System.out.println("暂无未完成任务");
        }
    }

    /**
     * 查询未完成天数
     */
    private void handleUnfinishedDays(String userId) {
        List<TaskDailyRecord> dailyRecords = dailyDao.findByUserId(userId);
        Map<String, Integer> unfinishedMap = new HashMap<>();
        Map<String, String> titleMap = new HashMap<>();

        for (TaskDailyRecord record : dailyRecords) {
            if (record.isOverdue()) {
                String taskId = record.getTaskId();
                unfinishedMap.put(taskId, unfinishedMap.getOrDefault(taskId, 0) + 1);
            }
        }

        List<Task> tasks = taskDao.findByUserId(userId);
        for (Task task : tasks) {
            titleMap.put(task.getId(), task.getTitle());
        }

        System.out.println("========== 未完成天数 ==========");
        if (unfinishedMap.isEmpty()) {
            System.out.println("暂无未完成天数");
            return;
        }
        for (Map.Entry<String, Integer> entry : unfinishedMap.entrySet()) {
            String title = titleMap.getOrDefault(entry.getKey(), "未知任务");
            System.out.println(title + "：未完成 " + entry.getValue() + " 天");
        }
    }

    /**
     * 格式化打印记录列表
     */
    private void printRecordList(List<PomodoroRecord> records) {
        if (records.isEmpty()) {
            System.out.println("暂无相关记录");
            return;
        }
        System.out.println("ID\t任务ID\t开始时间\t\t时长\t状态");
        for (PomodoroRecord record : records) {
            String status = record.getStatus() == com.pomodoro.model.enums.RecordStatus.FINISHED ? "已完成" : "已放弃";
            System.out.println(record.getId() + "\t" + record.getTaskId() + "\t"
                    + DateUtil.formatDateTime(record.getStartTime()) + "\t"
                    + DateUtil.formatDuration(record.getDuration()) + "\t" + status);
        }
    }
}
