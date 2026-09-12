package com.pomodoro.dao.impl;

import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每日任务记录数据访问实现类，基于 JDBC PreparedStatement 实现 task_daily_records 表的增删改查
 */
public class TaskDailyRecordDaoImpl implements TaskDailyRecordDao {

    /** 插入每日记录时需要写入的全部字段，一次写入 7 个字段 */
    private static final String INSERT_SQL =
            "INSERT INTO task_daily_records (id, task_id, user_id, record_date, target_count, "
                    + "completed_count, overdue) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /** 查询每日记录时使用的字段列表 */
    private static final String COLUMNS =
            "id, task_id, user_id, record_date, target_count, completed_count, overdue";

    /**
     * 插入一条每日记录，内部生成 D001 格式 ID 并写回实体
     */
    @Override
    public void insert(TaskDailyRecord record) {
        // 先生成下一个可用 ID 并写回实体
        record.setId(generateId());
        executeInsert(record);
    }

    /**
     * 插入一条保留实体自带 ID 的每日记录（用于数据恢复），不做 ID 生成
     */
    @Override
    public void insertWithId(TaskDailyRecord record) {
        executeInsert(record);
    }

    /**
     * 执行每日记录插入的具体数据库操作，供 insert 与 insertWithId 复用
     */
    private void executeInsert(TaskDailyRecord record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setString(1, record.getId());
            pstmt.setString(2, record.getTaskId());
            pstmt.setString(3, record.getUserId());
            pstmt.setDate(4, toSqlDate(record.getRecordDate()));
            setNullableInt(pstmt, 5, record.getTargetCount());
            pstmt.setInt(6, record.getCompletedCount());
            pstmt.setBoolean(7, record.isOverdue());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("保存每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 更新每日记录的全部字段
     */
    @Override
    public void update(TaskDailyRecord record) {
        String sql = "UPDATE task_daily_records SET task_id = ?, user_id = ?, record_date = ?, "
                + "target_count = ?, completed_count = ?, overdue = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getTaskId());
            pstmt.setString(2, record.getUserId());
            pstmt.setDate(3, toSqlDate(record.getRecordDate()));
            setNullableInt(pstmt, 4, record.getTargetCount());
            pstmt.setInt(5, record.getCompletedCount());
            pstmt.setBoolean(6, record.isOverdue());
            pstmt.setString(7, record.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("更新的每日记录不存在：" + record.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("更新每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 物理删除每日记录
     */
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM task_daily_records WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 查询每日记录，找不到时返回 null
     */
    @Override
    public TaskDailyRecord findById(String id) {
        String sql = "SELECT " + COLUMNS + " FROM task_daily_records WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询全部每日记录列表，没有数据时返回空 List
     */
    @Override
    public List<TaskDailyRecord> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM task_daily_records";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TaskDailyRecord> records = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(mapRow(rs));
            }
            return records;
        } catch (SQLException e) {
            throw new RuntimeException("查询每日记录列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 删除全部每日记录
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM task_daily_records";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("清空每日记录数据失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 生成下一个可用的每日记录 ID（D001 格式），表为空时返回 D001
     */
    @Override
    public synchronized String generateId() {
        // 取去掉首字母前缀后的数字最大值，加 1 后按 D%03d 补零生成
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) FROM task_daily_records";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                int max = rs.getInt(1);
                return String.format("D%03d", max + 1);
            }
            return "D001";
        } catch (SQLException e) {
            throw new RuntimeException("生成每日记录 ID 失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 按任务 ID 与日期查询当日的每日记录，找不到时返回 null
     */
    @Override
    public TaskDailyRecord findByTaskAndDate(String taskId, Date date) {
        String sql = "SELECT " + COLUMNS + " FROM task_daily_records WHERE task_id = ? AND record_date = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, taskId);
            pstmt.setDate(2, toSqlDate(date));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("按日期查询每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询指定用户的全部每日记录，没有数据时返回空 List
     */
    @Override
    public List<TaskDailyRecord> findByUserId(String userId) {
        String sql = "SELECT " + COLUMNS + " FROM task_daily_records WHERE user_id = ? ORDER BY record_date";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TaskDailyRecord> records = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(mapRow(rs));
            }
            return records;
        } catch (SQLException e) {
            throw new RuntimeException("查询用户每日记录失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 更新指定每日记录的当日已完成次数
     */
    @Override
    public void updateCompletedCount(String id, int count) {
        String sql = "UPDATE task_daily_records SET completed_count = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, count);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新每日记录完成次数失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 将指定每日记录标记为已逾期
     */
    @Override
    public void markOverdue(String id) {
        String sql = "UPDATE task_daily_records SET overdue = 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("标记每日记录逾期失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 将结果集当前行映射为 TaskDailyRecord 对象
     */
    private TaskDailyRecord mapRow(ResultSet rs) throws SQLException {
        TaskDailyRecord record = new TaskDailyRecord();
        record.setId(rs.getString("id"));
        record.setTaskId(rs.getString("task_id"));
        record.setUserId(rs.getString("user_id"));
        java.sql.Date recordDate = rs.getDate("record_date");
        if (recordDate != null) {
            record.setRecordDate(new Date(recordDate.getTime()));
        }
        int targetCount = rs.getInt("target_count");
        record.setTargetCount(rs.wasNull() ? 0 : targetCount);
        record.setCompletedCount(rs.getInt("completed_count"));
        record.setOverdue(rs.getBoolean("overdue"));
        return record;
    }

    /**
     * 将 java.util.Date 转换为 java.sql.Date，便于与 DATE 类型字段比较
     */
    private static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    /**
     * 设置可为空的整数参数，值为 null 时写入 NULL
     */
    private static void setNullableInt(PreparedStatement pstmt, int index, Integer value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.INTEGER);
        } else {
            pstmt.setInt(index, value);
        }
    }
}
