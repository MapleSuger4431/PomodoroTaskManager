package com.pomodoro.model;

import com.pomodoro.model.enums.Priority;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.model.enums.TimerMode;
import com.pomodoro.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务实体类，封装番茄任务管理系统中一个任务的完整信息
 */
public class Task implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private String id;

    /** 所属用户ID */
    private String userId;

    /** 任务标题 */
    private String title;

    /** 任务描述 */
    private String description;

    /** 标签，多个标签以逗号分隔 */
    private String tags;

    /** 优先级 */
    private Priority priority;

    /** 任务类型（按次数或按日期重复） */
    private TaskType type;

    /** 计时模式（倒计时或正计时） */
    private TimerMode timerMode;

    /** 目标完成次数 */
    private int targetCount;

    /** 已完成次数 */
    private int completedCount;

    /** 单个番茄的时长（分钟） */
    private int pomodoroMinutes;

    /** 截止日期 */
    private Date dueDate;

    /** 开始日期 */
    private Date startDate;

    /** 重复周期（天），0 表示不重复 */
    private int repeatDays;

    /** 每日截止时间，格式 HH:mm */
    private String dailyDeadline;

    /** 任务状态 */
    private TaskStatus status;

    /** 创建时间 */
    private Date createTime;

    /** 完成时间 */
    private Date completeTime;

    /** 逻辑删除标记 */
    private boolean deleted;

    /**
     * 无参构造方法
     */
    public Task() {
    }

    /**
     * 全参构造方法
     */
    public Task(String id, String userId, String title, String description, String tags,
                Priority priority, TaskType type, TimerMode timerMode,
                int targetCount, int completedCount, int pomodoroMinutes,
                Date dueDate, Date startDate, int repeatDays, String dailyDeadline,
                TaskStatus status, Date createTime, Date completeTime, boolean deleted) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.priority = priority;
        this.type = type;
        this.timerMode = timerMode;
        this.targetCount = targetCount;
        this.completedCount = completedCount;
        this.pomodoroMinutes = pomodoroMinutes;
        this.dueDate = dueDate;
        this.startDate = startDate;
        this.repeatDays = repeatDays;
        this.dailyDeadline = dailyDeadline;
        this.status = status;
        this.createTime = createTime;
        this.completeTime = completeTime;
        this.deleted = deleted;
    }

    /**
     * 获取任务ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置任务ID
     */
    public void setId(String id) {
        this.id = id;
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
     * 获取任务标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置任务标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取任务描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置任务描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取标签
     */
    public String getTags() {
        return tags;
    }

    /**
     * 设置标签
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 获取优先级
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * 获取任务类型
     */
    public TaskType getType() {
        return type;
    }

    /**
     * 设置任务类型
     */
    public void setType(TaskType type) {
        this.type = type;
    }

    /**
     * 获取计时模式
     */
    public TimerMode getTimerMode() {
        return timerMode;
    }

    /**
     * 设置计时模式
     */
    public void setTimerMode(TimerMode timerMode) {
        this.timerMode = timerMode;
    }

    /**
     * 获取目标完成次数
     */
    public int getTargetCount() {
        return targetCount;
    }

    /**
     * 设置目标完成次数
     */
    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    /**
     * 获取已完成次数
     */
    public int getCompletedCount() {
        return completedCount;
    }

    /**
     * 设置已完成次数
     */
    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    /**
     * 获取单个番茄的时长（分钟）
     */
    public int getPomodoroMinutes() {
        return pomodoroMinutes;
    }

    /**
     * 设置单个番茄的时长（分钟）
     */
    public void setPomodoroMinutes(int pomodoroMinutes) {
        this.pomodoroMinutes = pomodoroMinutes;
    }

    /**
     * 获取截止日期
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * 设置截止日期
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 获取开始日期
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置开始日期
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取重复周期（天）
     */
    public int getRepeatDays() {
        return repeatDays;
    }

    /**
     * 设置重复周期（天）
     */
    public void setRepeatDays(int repeatDays) {
        this.repeatDays = repeatDays;
    }

    /**
     * 获取每日截止时间（HH:mm）
     */
    public String getDailyDeadline() {
        return dailyDeadline;
    }

    /**
     * 设置每日截止时间（HH:mm）
     */
    public void setDailyDeadline(String dailyDeadline) {
        this.dailyDeadline = dailyDeadline;
    }

    /**
     * 获取任务状态
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * 设置任务状态
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取完成时间
     */
    public Date getCompleteTime() {
        return completeTime;
    }

    /**
     * 设置完成时间
     */
    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    /**
     * 判断任务是否已被逻辑删除
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 设置逻辑删除标记
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * 返回任务对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Task{id='" + id + "', userId='" + userId + "', title='" + title
                + "', description='" + description + "', tags='" + tags
                + "', priority=" + priority + ", type=" + type + ", timerMode=" + timerMode
                + ", targetCount=" + targetCount + ", completedCount=" + completedCount
                + ", pomodoroMinutes=" + pomodoroMinutes
                + ", dueDate=" + DateUtil.formatDate(dueDate)
                + ", startDate=" + DateUtil.formatDate(startDate)
                + ", repeatDays=" + repeatDays + ", dailyDeadline='" + dailyDeadline + "'"
                + ", status=" + status
                + ", createTime=" + DateUtil.formatDateTime(createTime)
                + ", completeTime=" + DateUtil.formatDateTime(completeTime)
                + ", deleted=" + deleted + "}";
    }
}
