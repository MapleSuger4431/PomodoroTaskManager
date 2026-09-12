package com.pomodoro.model;

import com.pomodoro.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日任务记录实体类，封装某个任务在某一天的目标与完成情况
 */
public class TaskDailyRecord implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;

    /** 关联的任务ID */
    private String taskId;

    /** 所属用户ID */
    private String userId;

    /** 记录日期 */
    private Date recordDate;

    /** 当日目标完成次数 */
    private int targetCount;

    /** 当日已完成次数 */
    private int completedCount;

    /** 当日是否已逾期 */
    private boolean overdue;

    /**
     * 无参构造方法
     */
    public TaskDailyRecord() {
    }

    /**
     * 全参构造方法
     */
    public TaskDailyRecord(String id, String taskId, String userId, Date recordDate,
                           int targetCount, int completedCount, boolean overdue) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.recordDate = recordDate;
        this.targetCount = targetCount;
        this.completedCount = completedCount;
        this.overdue = overdue;
    }

    /**
     * 获取记录ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置记录ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取关联的任务ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * 设置关联的任务ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取所属用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置所属用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取记录日期
     */
    public Date getRecordDate() {
        return recordDate;
    }

    /**
     * 设置记录日期
     */
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * 获取当日目标完成次数
     */
    public int getTargetCount() {
        return targetCount;
    }

    /**
     * 设置当日目标完成次数
     */
    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    /**
     * 获取当日已完成次数
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * 设置当日已完成次数
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    /**
     * 判断当日是否已逾期
     */
    public boolean isOverdue() {
        return overdue;
    }

    /**
     * 设置当日是否已逾期
     */
    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    /**
     * 返回每日任务记录对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "TaskDailyRecord{id='" + id + "', taskId='" + taskId + "', userId='" + userId
                + "', recordDate=" + DateUtil.formatDate(recordDate)
                + ", targetCount=" + targetCount + ", completedCount=" + completedCount
                + ", overdue=" + overdue + "}";
    }
}
