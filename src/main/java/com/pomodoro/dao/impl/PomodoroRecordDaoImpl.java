package com.pomodoro.dao.impl;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.model.PomodoroRecord;
import com.pomodoro.model.enums.RecordStatus;
import com.pomodoro.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 番茄记录DAO实现类
 */
public class PomodoroRecordDaoImpl implements PomodoroRecordDao {

    @Override
    public PomodoroRecord findById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PomodoroRecord record = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM pomodoro_records WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                record = mapRow(rs);
            }
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return record;
    }

    @Override
    public List<PomodoroRecord> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PomodoroRecord> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM pomodoro_records ORDER BY start_time DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public void insert(PomodoroRecord record) {
        record.setId(generateId());
        insertWithId(record);
    }

    @Override
    public void update(PomodoroRecord record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE pomodoro_records SET user_id=?, task_id=?, start_time=?, end_time=?, duration=?, status=? WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getUserId());
            pstmt.setString(2, record.getTaskId());
            pstmt.setTimestamp(3, new Timestamp(record.getStartTime().getTime()));
            pstmt.setTimestamp(4, new Timestamp(record.getEndTime().getTime()));
            pstmt.setInt(5, record.getDuration());
            pstmt.setInt(6, record.getStatus().getCode());
            pstmt.setString(7, record.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public void deleteById(String id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM pomodoro_records WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public void deleteAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM pomodoro_records";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public List<PomodoroRecord> findByUserId(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PomodoroRecord> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT r.* FROM pomodoro_records r JOIN tasks t ON r.task_id = t.id WHERE r.user_id = ? AND t.deleted = 0 ORDER BY r.start_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<PomodoroRecord> findByCondition(String userId, Date date, String taskId, String tag) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PomodoroRecord> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT r.* FROM pomodoro_records r");
            List<Object> params = new ArrayList<>();

            boolean needJoinTask = (tag != null && !tag.isEmpty());
            if (needJoinTask) {
                sql.append(" JOIN tasks t ON r.task_id = t.id");
            }

            sql.append(" WHERE r.user_id = ?");
            params.add(userId);

            if (date != null) {
                sql.append(" AND CAST(r.start_time AS DATE) = ?");
                params.add(new Date(date.getTime()));
            }

            if (taskId != null && !taskId.isEmpty()) {
                sql.append(" AND r.task_id = ?");
                params.add(taskId);
            }

            if (tag != null && !tag.isEmpty()) {
                sql.append(" AND t.tags LIKE ?");
                params.add("%" + tag + "%");
            }

            sql.append(" ORDER BY r.start_time DESC");

            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public void insertWithId(PomodoroRecord record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO pomodoro_records (id, user_id, task_id, start_time, end_time, duration, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, record.getId());
            pstmt.setString(2, record.getUserId());
            pstmt.setString(3, record.getTaskId());
            pstmt.setTimestamp(4, new Timestamp(record.getStartTime().getTime()));
            pstmt.setTimestamp(5, new Timestamp(record.getEndTime().getTime()));
            pstmt.setInt(6, record.getDuration());
            pstmt.setInt(7, record.getStatus().getCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public synchronized String generateId() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String newId = "R001";
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) AS max_num FROM pomodoro_records";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                if (!rs.wasNull()) {
                    newId = String.format("R%03d", maxNum + 1);
                }
            }
        } catch (SQLException e) {
            // 不再吞掉异常静默失败：数据库操作失败时向上抛出运行时异常，避免记录静默丢失
            throw new RuntimeException("番茄钟记录数据库操作失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return newId;
    }

    /**
     * 结果集映射为实体对象
     */
    private PomodoroRecord mapRow(ResultSet rs) throws SQLException {
        PomodoroRecord record = new PomodoroRecord();
        record.setId(rs.getString("id"));
        record.setUserId(rs.getString("user_id"));
        record.setTaskId(rs.getString("task_id"));
        record.setStartTime(rs.getTimestamp("start_time"));
        record.setEndTime(rs.getTimestamp("end_time"));
        record.setDuration(rs.getInt("duration"));
        record.setStatus(RecordStatus.fromCode(rs.getInt("status")));
        return record;
    }
}
