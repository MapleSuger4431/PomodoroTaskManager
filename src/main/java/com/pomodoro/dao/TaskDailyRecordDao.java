package com.pomodoro.dao;

import com.pomodoro.model.TaskDailyRecord;

import java.util.Date;
import java.util.List;

/**
 * 每日任务记录数据访问接口，定义每日任务记录表的专属操作
 */
public interface TaskDailyRecordDao extends BaseDao<TaskDailyRecord> {

    /**
     * 按任务 ID 与日期查询当日的每日记录
     */
    TaskDailyRecord findByTaskAndDate(String taskId, Date date);

    /**
     * 查询指定用户的全部每日记录
     */
    List<TaskDailyRecord> findByUserId(String userId);

    /**
     * 更新指定每日记录的已完成次数
     */
    void updateCompletedCount(String id, int count);

    /**
     * 将指定每日记录标记为已逾期
     */
    void markOverdue(String id);

    /**
     * 插入一条使用指定 ID 的每日记录
     */
    void insertWithId(TaskDailyRecord record);
}
