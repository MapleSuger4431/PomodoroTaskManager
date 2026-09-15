package com.pomodoro.service;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.dao.UserDao;
import com.pomodoro.dao.impl.PomodoroRecordDaoImpl;
import com.pomodoro.dao.impl.TaskDaoImpl;
import com.pomodoro.dao.impl.TaskDailyRecordDaoImpl;
import com.pomodoro.dao.impl.UserDaoImpl;
import com.pomodoro.model.BackupData;
import com.pomodoro.model.PomodoroRecord;
import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.User;
import com.pomodoro.util.DBUtil;
import com.pomodoro.util.SerializeUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 备份业务逻辑类，负责系统数据的序列化备份与恢复
 * 恢复时先删子表再删父表，插入时先插父表再插子表，且必须按原 ID 插入以保证外键关系不断
 */
public class BackupService {

    /** 用户数据访问对象 */
    private UserDao userDao = new UserDaoImpl();

    /** 任务数据访问对象 */
    private TaskDao taskDao = new TaskDaoImpl();

    /** 番茄钟记录数据访问对象 */
    private PomodoroRecordDao recordDao = new PomodoroRecordDaoImpl();

    /** 每日任务记录数据访问对象 */
    private TaskDailyRecordDao dailyDao = new TaskDailyRecordDaoImpl();

    /**
     * 备份全部用户的系统数据到文件（所有用户共用同一份全量备份）
     * users 表全量备份，其余三张表遍历每个用户汇总，保证恢复后所有用户数据完整
     */
    public void backup(String filePath) {
        try {
            BackupData data = new BackupData();
            data.setUsers(userDao.findAll());
            // 遍历每个用户，汇总其任务、每日记录与番茄钟记录，实现全用户备份
            for (User u : data.getUsers()) {
                String uid = u.getId();
                data.getTasks().addAll(taskDao.findByUserId(uid));
                data.getRecords().addAll(recordDao.findByUserId(uid));
                data.getDailyRecords().addAll(dailyDao.findByUserId(uid));
            }
            // 自定义路径支持：若路径中的上级目录不存在则自动创建，保证任意自定义路径可用
            File file = new File(filePath);
            File parent = file.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            SerializeUtil.writeObject(data, filePath);
            System.out.println("备份成功！已备份全部用户数据，文件路径：" + file.getAbsoluteFile());
        } catch (Exception e) {
            System.out.println("备份失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从备份文件恢复全部数据
     * 删除顺序（先删子表）：pomodoro_records -> task_daily_records -> tasks -> users
     * 插入顺序（先插父表）：users -> tasks -> task_daily_records -> pomodoro_records
     */
    public void restore(String filePath) {
        try {
            BackupData data = (BackupData) SerializeUtil.readObject(filePath);
            // 第一步：清空现有数据，按先删子表的顺序，避免外键冲突
            recordDao.deleteAll();
            dailyDao.deleteAll();
            taskDao.deleteAll();
            userDao.deleteAll();
            // 第二步：按先插父表的顺序恢复数据，必须保留原 ID，否则外键会断
            for (User u : data.getUsers()) {
                // UserDao 接口没有 insertWithId，这里直接按原 ID 插入用户记录
                insertUserWithId(u);
            }
            for (Task t : data.getTasks()) {
                taskDao.insertWithId(t);
            }
            for (TaskDailyRecord d : data.getDailyRecords()) {
                dailyDao.insertWithId(d);
            }
            for (PomodoroRecord r : data.getRecords()) {
                recordDao.insertWithId(r);
            }
            System.out.println("恢复成功！数据已从文件 " + filePath + " 恢复");
        } catch (Exception e) {
            System.out.println("恢复失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 按原 ID 插入用户记录，等同于 insertWithId 的语义（用于数据恢复，不重新生成 ID）
     *
     * @param user 用户对象，ID 取自对象本身
     */
    private void insertUserWithId(User user) {
        String sql = "INSERT INTO users (id, username, password, create_time) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            Date createTime = user.getCreateTime();
            if (createTime != null) {
                pstmt.setTimestamp(4, new Timestamp(createTime.getTime()));
            } else {
                pstmt.setTimestamp(4, null);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("按指定 ID 插入用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
}
