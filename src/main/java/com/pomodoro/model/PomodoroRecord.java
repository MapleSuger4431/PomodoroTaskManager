package com.pomodoro.model;

import com.pomodoro.model.enums.RecordStatus;
import com.pomodoro.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 番茄钟记录实体类，封装一次番茄钟计时的完整记录
 */
public class PomodoroRecord implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;

    /** 所属用户ID */
    private String userId;

    /** 关联的任务ID */
    private String taskId;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 有效计时时长（秒） */
    private int duration;

    /** 记录状态（完成或放弃） */
    private RecordStatus status;

    /**
     * 无参构造方法
     */
    public PomodoroRecord() {
    }

    /**
     * 全参构造方法
     */
    public PomodoroRecord(String id, String userId, String taskId, Date startTime, Date endTime,
                          int duration, RecordStatus status) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.status = status;
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
     * 获取开始时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取有效计时时长（秒）
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置有效计时时长（秒）
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 获取记录状态
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * 设置记录状态
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    /**
     * 返回番茄钟记录对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "PomodoroRecord{id='" + id + "', userId='" + userId + "', taskId='" + taskId
                + "', startTime=" + DateUtil.formatDateTime(startTime)
                + ", endTime=" + DateUtil.formatDateTime(endTime)
                + ", duration=" + duration + ", status=" + status + "}";
    }
}
