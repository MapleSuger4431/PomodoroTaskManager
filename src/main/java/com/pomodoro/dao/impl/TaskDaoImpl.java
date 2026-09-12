package com.pomodoro.dao.impl;

import com.pomodoro.dao.TaskDao;
import com.pomodoro.model.Task;
import com.pomodoro.model.enums.Priority;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.model.enums.TimerMode;
import com.pomodoro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务数据访问实现类，基于 JDBC PreparedStatement 实现 tasks 表的增删改查
 */
public class TaskDaoImpl implements TaskDao {

    /** 插入任务时需要写入的全部字段，一次写入 19 个字段 */
    private static final String INSERT_SQL =
            "INSERT INTO tasks (id, user_id, title, description, tags, priority, type, timer_mode, "
                    + "target_count, completed_count, pomodoro_minutes, due_date, start_date, repeat_days, "
                    + "daily_deadline, status, create_time, complete_time, deleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** 查询任务时使用的字段列表 */
    private static final String COLUMNS =
            "id, user_id, title, description, tags, priority, type, timer_mode, target_count, "
                    + "completed_count, pomodoro_minutes, due_date, start_date, repeat_days, "
                    + "daily_deadline, status, create_time, complete_time, deleted";

    /**
     * 插入一条任务记录，内部生成 T001 格式 ID 并写回实体
     */
    @Override
    public void insert(Task task) {
        // 先生成下一个可用 ID 并写回实体，创建时间使用当前系统时间
        task.setId(generateId());
        Date createTime = new Date();
        task.setCreateTime(createTime);
        executeInsert(task, createTime);
    }

    /**
     * 插入一条保留实体自带 ID 的任务记录（用于数据恢复），不做 ID 生成
     */
    @Override
    public void insertWithId(Task task) {
        // 恢复数据时必须保留原 ID，外键关联才不会断；创建时间为空时补当前系统时间
        Date createTime = task.getCreateTime() != null ? task.getCreateTime() : new Date();
        task.setCreateTime(createTime);
        executeInsert(task, createTime);
    }

    /**
     * 执行任务插入的具体数据库操作，供 insert 与 insertWithId 复用
     */
    private void executeInsert(Task task, Date createTime) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL);
            pstmt.setString(1, task.getId());
            pstmt.setString(2, task.getUserId());
            pstmt.setString(3, task.getTitle());
            setNullableString(pstmt, 4, task.getDescription());
            setNullableString(pstmt, 5, task.getTags());
            pstmt.setInt(6, task.getPriority() != null ? task.getPriority().getCode() : Priority.MEDIUM.getCode());
            pstmt.setInt(7, task.getType() != null ? task.getType().getCode() : TaskType.REPEAT_COUNT.getCode());
            pstmt.setInt(8, task.getTimerMode() != null ? task.getTimerMode().getCode() : TimerMode.COUNTDOWN.getCode());
            pstmt.setInt(9, task.getTargetCount());
            pstmt.setInt(10, task.getCompletedCount());
            pstmt.setInt(11, task.getPomodoroMinutes());
            setNullableTimestamp(pstmt, 12, task.getDueDate());
            setNullableTimestamp(pstmt, 13, task.getStartDate());
            pstmt.setInt(14, task.getRepeatDays());
            setNullableString(pstmt, 15, task.getDailyDeadline());
            pstmt.setInt(16, task.getStatus() != null ? task.getStatus().getCode() : TaskStatus.TODO.getCode());
            pstmt.setTimestamp(17, new Timestamp(createTime.getTime()));
            setNullableTimestamp(pstmt, 18, task.getCompleteTime());
            pstmt.setBoolean(19, task.isDeleted());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("保存任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 更新任务的全部字段
     */
    @Override
    public void update(Task task) {
        String sql = "UPDATE tasks SET user_id = ?, title = ?, description = ?, tags = ?, priority = ?, "
                + "type = ?, timer_mode = ?, target_count = ?, completed_count = ?, pomodoro_minutes = ?, "
                + "due_date = ?, start_date = ?, repeat_days = ?, daily_deadline = ?, status = ?, "
                + "complete_time = ?, deleted = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, task.getUserId());
            pstmt.setString(2, task.getTitle());
            setNullableString(pstmt, 3, task.getDescription());
            setNullableString(pstmt, 4, task.getTags());
            pstmt.setInt(5, task.getPriority() != null ? task.getPriority().getCode() : Priority.MEDIUM.getCode());
            pstmt.setInt(6, task.getType() != null ? task.getType().getCode() : TaskType.REPEAT_COUNT.getCode());
            pstmt.setInt(7, task.getTimerMode() != null ? task.getTimerMode().getCode() : TimerMode.COUNTDOWN.getCode());
            pstmt.setInt(8, task.getTargetCount());
            pstmt.setInt(9, task.getCompletedCount());
            pstmt.setInt(10, task.getPomodoroMinutes());
            setNullableTimestamp(pstmt, 11, task.getDueDate());
            setNullableTimestamp(pstmt, 12, task.getStartDate());
            pstmt.setInt(13, task.getRepeatDays());
            setNullableString(pstmt, 14, task.getDailyDeadline());
            pstmt.setInt(15, task.getStatus() != null ? task.getStatus().getCode() : TaskStatus.TODO.getCode());
            setNullableTimestamp(pstmt, 16, task.getCompleteTime());
            pstmt.setBoolean(17, task.isDeleted());
            pstmt.setString(18, task.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("更新的任务不存在：" + task.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("更新任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 物理删除任务记录
     */
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 查询任务，找不到时返回 null
     */
    @Override
    public Task findById(String id) {
        String sql = "SELECT " + COLUMNS + " FROM tasks WHERE id = ?";
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
            throw new RuntimeException("查询任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询全部任务列表，没有数据时返回空 List
     */
    @Override
    public List<Task> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM tasks";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("查询任务列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 删除全部任务记录
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM tasks";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("清空任务数据失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 生成下一个可用的任务 ID（T001 格式），表为空时返回 T001
     */
    @Override
    public synchronized String generateId() {
        // 取去掉首字母前缀后的数字最大值，加 1 后按 T%03d 补零生成
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) FROM tasks";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                int max = rs.getInt(1);
                return String.format("T%03d", max + 1);
            }
            return "T001";
        } catch (SQLException e) {
            throw new RuntimeException("生成任务 ID 失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询指定用户的全部任务（含已逻辑删除），没有数据时返回空 List
     */
    @Override
    public List<Task> findByUserId(String userId) {
        String sql = "SELECT " + COLUMNS + " FROM tasks WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("查询用户任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询指定用户未被逻辑删除的任务，按创建时间倒序排列
     */
    @Override
    public List<Task> findNotDeletedByUserId(String userId) {
        String sql = "SELECT " + COLUMNS + " FROM tasks WHERE user_id = ? AND deleted = 0 ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("查询用户任务列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询指定用户按日期重复且未完成未删除的任务，用于刷新每日记录
     */
    @Override
    public List<Task> findRepeatDateTasksByUser(String userId) {
        String sql = "SELECT " + COLUMNS + " FROM tasks WHERE user_id = ? AND type = 1 AND status <> 1 AND deleted = 0";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Task> tasks = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("查询按日期重复任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 逻辑删除指定任务：列表不再展示，历史数据保留在数据库中
     */
    @Override
    public void logicDelete(String taskId) {
        String sql = "UPDATE tasks SET deleted = 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("逻辑删除任务失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 更新指定任务的已完成次数
     */
    @Override
    public void updateCompletedCount(String taskId, int count) {
        String sql = "UPDATE tasks SET completed_count = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, count);
            pstmt.setString(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新任务完成次数失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 将指定任务标记为已完成，同时记录完成时间
     */
    @Override
    public void markDone(String taskId) {
        String sql = "UPDATE tasks SET status = 1, complete_time = GETDATE() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("标记任务完成失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 将结果集当前行映射为 Task 对象
     */
    private Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getString("id"));
        task.setUserId(rs.getString("user_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setTags(rs.getString("tags"));
        task.setPriority(Priority.fromCode(rs.getInt("priority")));
        task.setType(TaskType.fromCode(rs.getInt("type")));
        task.setTimerMode(TimerMode.fromCode(rs.getInt("timer_mode")));
        task.setTargetCount(rs.getInt("target_count"));
        task.setCompletedCount(rs.getInt("completed_count"));
        task.setPomodoroMinutes(rs.getInt("pomodoro_minutes"));
        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            task.setDueDate(new Date(dueDate.getTime()));
        }
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) {
            task.setStartDate(new Date(startDate.getTime()));
        }
        task.setRepeatDays(rs.getInt("repeat_days"));
        task.setDailyDeadline(rs.getString("daily_deadline"));
        task.setStatus(TaskStatus.fromCode(rs.getInt("status")));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            task.setCreateTime(new Date(createTime.getTime()));
        }
        Timestamp completeTime = rs.getTimestamp("complete_time");
        if (completeTime != null) {
            task.setCompleteTime(new Date(completeTime.getTime()));
        }
        task.setDeleted(rs.getBoolean("deleted"));
        return task;
    }

    /**
     * 设置可为空的字符串参数，值为 null 时写入 NULL
     */
    private static void setNullableString(PreparedStatement pstmt, int index, String value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.NVARCHAR);
        } else {
            pstmt.setString(index, value);
        }
    }

    /**
     * 设置可为空的日期时间参数，值为 null 时写入 NULL
     */
    private static void setNullableTimestamp(PreparedStatement pstmt, int index, Date value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.TIMESTAMP);
        } else {
            pstmt.setTimestamp(index, new Timestamp(value.getTime()));
        }
    }
}
