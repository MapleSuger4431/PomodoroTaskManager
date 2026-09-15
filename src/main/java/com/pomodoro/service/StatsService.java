package com.pomodoro.service;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.dao.impl.PomodoroRecordDaoImpl;
import com.pomodoro.dao.impl.TaskDaoImpl;
import com.pomodoro.dao.impl.TaskDailyRecordDaoImpl;
import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.util.DBUtil;
import com.pomodoro.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 统计业务逻辑类，负责番茄完成数、专注时长与未完成数的汇总统计
 * 统计口径说明：
 * - 完成次数/专注时长：pomodoro_records 中 status=FINISHED 的记录，不加 deleted 过滤（含已删除任务的历史数据）
 * - 本周：周一 00:00:00 至周日 23:59:59
 * - 未完成：未删除的 REPEAT_COUNT 任务中未达标且未标记完成的计 1 次；REPEAT_DATE 任务按每日记录 overdue 条数累计
 */
public class StatsService {

    /** 任务数据访问对象 */
    private TaskDao taskDao = new TaskDaoImpl();

    /** 番茄钟记录数据访问对象 */
    private PomodoroRecordDao recordDao = new PomodoroRecordDaoImpl();

    /** 每日任务记录数据访问对象 */
    private TaskDailyRecordDao dailyDao = new TaskDailyRecordDaoImpl();

    /**
     * 统计用户累计完成的番茄总数（status=FINISHED 的记录数）
     */
    public int getTotalCompletedCount(String userId) {
        String sql = "SELECT COUNT(*) FROM pomodoro_records WHERE user_id = ? AND status = 0";
        return querySingleInt(sql, userId);
    }

    /**
     * 统计用户本周完成的番茄数（start_time 在本周内的 FINISHED 记录数）
     */
    public int getWeeklyCompletedCount(String userId) {
        Date now = new Date();
        String sql = "SELECT COUNT(*) FROM pomodoro_records WHERE user_id = ? AND status = 0"
                + " AND start_time >= ? AND start_time <= ?";
        return querySingleInt(sql, userId, DateUtil.getWeekStart(now), DateUtil.getWeekEnd(now));
    }

    /**
     * 统计用户累计专注总时长（FINISHED 记录的 duration 秒数总和）
     */
    public int getTotalFocusDuration(String userId) {
        String sql = "SELECT ISNULL(SUM(duration), 0) FROM pomodoro_records WHERE user_id = ? AND status = 0";
        return querySingleInt(sql, userId);
    }

    /**
     * 统计用户本周专注时长（本周内 FINISHED 记录的 duration 秒数总和）
     */
    public int getWeeklyFocusDuration(String userId) {
        Date now = new Date();
        String sql = "SELECT ISNULL(SUM(duration), 0) FROM pomodoro_records WHERE user_id = ? AND status = 0"
                + " AND start_time >= ? AND start_time <= ?";
        return querySingleInt(sql, userId, DateUtil.getWeekStart(now), DateUtil.getWeekEnd(now));
    }

    /**
     * 统计用户未完成的任务数，展示用词统一为"未完成"
     * 口径与 RecordMenu 的"查询未完成任务"保持一致：
     * 1. REPEAT_COUNT 任务：完成次数未达目标且未被手动标记完成，每个任务计 1 次（不再依赖截止时间）
     * 2. REPEAT_DATE 任务：task_daily_records 中 overdue=true 的记录数，每条计 1 次
     * 已逻辑删除的任务不再计入，避免删除任务后统计数字不减少的观感问题
     */
    public int getUnfinishedCount(String userId) {
        int count = 0;
        // 第一部分：按次数任务，完成次数未达目标且未被手动标记完成
        List<Task> tasks = taskDao.findNotDeletedByUserId(userId);
        for (Task task : tasks) {
            if (task.getType() == TaskType.REPEAT_COUNT
                    && task.getCompletedCount() < task.getTargetCount()
                    && task.getStatus() != TaskStatus.DONE) {
                count++;
            }
        }
        // 第二部分：按日期任务，每日记录中已逾期的条数
        List<TaskDailyRecord> dailyRecords = dailyDao.findByUserId(userId);
        for (TaskDailyRecord record : dailyRecords) {
            if (record.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 执行单值统计查询，返回查询结果的第一行第一列整数值
     *
     * @param sql    统计 SQL，使用 ? 占位
     * @param params 占位参数，按顺序绑定
     * @return 统计结果，查询失败或无数据时返回 0
     */
    private int querySingleInt(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}
