package com.pomodoro.dao;

import com.pomodoro.model.PomodoroRecord;

import java.util.Date;
import java.util.List;

/**
 * 番茄钟记录数据访问接口，定义番茄钟记录表的专属操作
 */
public interface PomodoroRecordDao extends BaseDao<PomodoroRecord> {

    /**
     * 查询指定用户的全部番茄钟记录
     */
    List<PomodoroRecord> findByUserId(String userId);

    /**
     * 按用户、日期、任务 ID 与标签的组合条件查询番茄钟记录
     */
    List<PomodoroRecord> findByCondition(String userId, Date date, String taskId, String tag);

    /**
     * 插入一条使用指定 ID 的番茄钟记录
     */
    void insertWithId(PomodoroRecord record);
}
