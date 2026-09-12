package com.pomodoro.dao.impl;

import com.pomodoro.dao.UserDao;
import com.pomodoro.model.User;
import com.pomodoro.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户数据访问实现类，基于 JDBC PreparedStatement 实现 users 表的增删改查
 */
public class UserDaoImpl implements UserDao {

    /**
     * 插入一条用户记录，内部生成 U001 格式 ID 并写回实体
     */
    @Override
    public void insert(User user) {
        String sql = "INSERT INTO users (id, username, password, create_time) VALUES (?, ?, ?, ?)";
        // 先生成下一个可用 ID 并写回实体，创建时间使用当前系统时间
        user.setId(generateId());
        Date createTime = new Date();
        user.setCreateTime(createTime);
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setTimestamp(4, new Timestamp(createTime.getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("保存用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 更新用户的用户名和密码
     */
    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("更新的用户不存在：" + user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("更新用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 删除用户记录
     */
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据 ID 查询用户，找不到时返回 null
     */
    @Override
    public User findById(String id) {
        String sql = "SELECT id, username, password, create_time FROM users WHERE id = ?";
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
            throw new RuntimeException("查询用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询全部用户列表，没有数据时返回空 List
     */
    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, password, create_time FROM users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("查询用户列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 删除全部用户记录
     */
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("清空用户数据失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 生成下一个可用的用户 ID（U001 格式），表为空时返回 U001
     */
    @Override
    public synchronized String generateId() {
        // 取去掉首字母前缀后的数字最大值，加 1 后按 U%03d 补零生成
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) FROM users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                int max = rs.getInt(1);
                return String.format("U%03d", max + 1);
            }
            return "U001";
        } catch (SQLException e) {
            throw new RuntimeException("生成用户 ID 失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 根据用户名查询用户，找不到时返回 null
     */
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, create_time FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("根据用户名查询用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 将结果集当前行映射为 User 对象
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            user.setCreateTime(new Date(createTime.getTime()));
        }
        return user;
    }
}
