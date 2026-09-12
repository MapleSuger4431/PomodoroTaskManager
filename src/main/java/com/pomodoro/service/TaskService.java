package com.pomodoro.service;

import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.dao.impl.TaskDaoImpl;
import com.pomodoro.dao.impl.TaskDailyRecordDaoImpl;
import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务业务逻辑类，负责任务的增删改查与每日记录刷新
 */
public class TaskService {

    /** 任务数据访问对象 */
    private TaskDao taskDao = new TaskDaoImpl();

    /** 每日任务记录数据访问对象 */
    private TaskDailyRecordDao dailyDao = new TaskDailyRecordDaoImpl();

    /**
     * 创建新任务：初始化默认状态后保存，按日期重复任务立即补齐当天记录
     */
    public void createTask(Task task) {
        // 新任务的初始状态统一在业务层设置，避免界面层遗漏
        task.setStatus(TaskStatus.TODO);
        task.setCompletedCount(0);
        task.setDeleted(false);
        task.setCreateTime(new Date());
        // 按日期重复任务补默认值：每日截止时间默认 23:59，开始日期默认今天
        if (task.getType() == TaskType.REPEAT_DATE) {
            if (task.getDailyDeadline() == null || task.getDailyDeadline().trim().isEmpty()) {
                task.setDailyDeadline("23:59");
            }
            if (task.getStartDate() == null) {
                task.setStartDate(new Date());
            }
        }
        taskDao.insert(task);
        // 按日期重复任务创建后立即生成今天的每日记录
        if (task.getType() == TaskType.REPEAT_DATE) {
            refreshDailyRecords(task.getUserId());
        }
    }

    /**
     * 更新任务信息
     */
    public void updateTask(Task task) {
        taskDao.update(task);
    }

    /**
     * 删除任务：使用逻辑删除，列表不再展示但历史数据保留
     */
    public void deleteTask(String taskId) {
        taskDao.logicDelete(taskId);
    }

    /**
     * 将任务标记为已完成
     */
    public void markDone(String taskId) {
        taskDao.markDone(taskId);
    }

    /**
     * 查询用户的任务列表：查询前先刷新每日记录，保证进度与状态是最新的
     */
    public List<Task> listTasks(String userId) {
        refreshDailyRecords(userId);
        return taskDao.findNotDeletedByUserId(userId);
    }

    /**
     * 刷新用户的每日任务记录：为按日期重复的任务补齐缺失的每日记录，并标记已过截止时间未完成的记录
     */
    public void refreshDailyRecords(String userId) {
        // 只处理按日期重复且未完成未删除的任务，已完成或已删除的任务不再生成记录
        List<Task> tasks = taskDao.findRepeatDateTasksByUser(userId);
        Date today = new Date();
        for (Task task : tasks) {
            Date day = task.getStartDate() != null ? task.getStartDate() : today;
            int repeatDays = task.getRepeatDays();
            int dayIndex = 0;
            // 从开始日期逐天遍历：有限任务最多遍历 repeatDays 天，无限任务（-1）遍历到今天为止
            while (!day.after(today)) {
                if (repeatDays != -1 && dayIndex >= repeatDays) {
                    break;
                }
                TaskDailyRecord record = dailyDao.findByTaskAndDate(task.getId(), day);
                if (record == null) {
                    // 当天还没有记录则补建一条，目标次数取自任务配置，只补到今天为止、不预生成未来
                    record = new TaskDailyRecord();
                    record.setTaskId(task.getId());
                    record.setUserId(userId);
                    record.setRecordDate(day);
                    record.setTargetCount(task.getTargetCount());
                    record.setCompletedCount(0);
                    record.setOverdue(false);
                    dailyDao.insert(record);
                }
                // 已过每日截止时间且当日完成次数未达目标的记录，标记为逾期
                if (!record.isOverdue()
                        && DateUtil.isPastDeadline(day, task.getDailyDeadline())
                        && record.getCompletedCount() < record.getTargetCount()) {
                    dailyDao.markOverdue(record.getId());
                }
                day = DateUtil.addDays(day, 1);
                dayIndex++;
            }
        }
    }

    /**
     * 查询可用于番茄钟计时的任务：未被删除且未完成的全部任务（不限任务类型）
     */
    public List<Task> listTimerSelectableTasks(String userId) {
        List<Task> all = taskDao.findNotDeletedByUserId(userId);
        List<Task> selectable = new ArrayList<>();
        for (Task task : all) {
            if (task.getStatus() != TaskStatus.DONE) {
                selectable.add(task);
            }
        }
        return selectable;
    }

    /**
     * 查询指定任务在指定日期的每日记录，找不到时返回 null（供界面展示当日进度）
     */
    public TaskDailyRecord getDailyRecord(String taskId, Date date) {
        return dailyDao.findByTaskAndDate(taskId, date);
    }

    /**
     * 查询指定用户的全部每日记录，没有数据时返回空 List（供界面统计已完成天数）
     */
    public List<TaskDailyRecord> listDailyRecords(String userId) {
        return dailyDao.findByUserId(userId);
    }
}
